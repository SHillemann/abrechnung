<script>
    import { Datepicker } from "svelte-calendar";
    import "dayjs/locale/de.js";
    import dayjs from "dayjs";
    import Column from "./Column.svelte";
    import { updateDate,offsetDate } from "../stores/store";

    export let columns;
    let locale = "de";
    let format = 'DD.MM.YYYY';
    let dateStore;
    
    let offsetDateValue;

    offsetDate.subscribe(value => {
        offsetDateValue = value;
    });

    $: dayjs.locale(locale);
    $: $dateStore?.selected ? updateDate(new Date($dateStore?.selected)) : () => {}

    const theme = {
		calendar: {
			colors: {
				background: {
					highlight: 'teal'
				}
			}
		}
	};

    async function updateRepo(buchungstag, items) {
        const res = await fetch("http://localhost:8081/abrechnung/updateTag", {
            method: "POST",
            body: JSON.stringify({
                buchungstag: buchungstag,
                items: items
            }),
            headers: {
            'Authorization': 'Basic ' + btoa('admin:password'),
            "content-type": "application/json",
        },
        });
    }

    function handleItemFinalize(columnIdx, newItems) {
        columns[columnIdx].buchungsblocks = newItems;
        updateRepo(columns[columnIdx].buchungstag, newItems);
    }
</script>
<div class="ui stackable three column grid">
    <div class="row">
        <div class="middle center aligned column">
        <h1 class="ui header"><b class="ui teal label big">A</b>brechnung</h1>
        </div>
        <div class="center aligned column">
            <button class="ui teal button" on:click={() => {dateStore.add(-7, 'day');offsetDate.update(n => n+7);}}>&lt;</button>
            <Datepicker {format} {theme} startOfWeekIndex={1} bind:store={dateStore} />            
            <button class="ui teal button" on:click={() => {dateStore.add(+7, 'day');offsetDate.update(n => n-7);}}>&gt;</button>
        </div>
        <div class="column"></div>
    </div>
    <div class="row" style="padding-top: 0">
        <div class="column"></div>
        <div class="center aligned column">
            <button class="ui teal button" on:click={() => {dateStore.add(offsetDateValue, 'day');offsetDate.set(0);}}>Heute</button>
        </div>
        <div class="column"></div>
    </div>
    <div class="row"></div>
</div>
<div class="ui stackable seven column internally celled grid ">
    <div class="row">
        {#each columns as {tag, buchungstag, buchungsblocks}, idx (buchungstag)}
            <div class="column">
                <Column
                    {tag}                
                    {buchungstag}
                    {buchungsblocks}
                    colIdx={idx}
                    onDrop={(newItems) => handleItemFinalize(idx, newItems)}
                />
            </div>
        {/each}
    </div>
</div>