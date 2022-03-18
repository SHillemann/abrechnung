<script>
    import { Datepicker } from "svelte-calendar";
    import "dayjs/locale/de.js";
    import dayjs from "dayjs";
    import Column from "./Column.svelte";
    import { nextWeek, prevWeek } from "../stores/store";
    import { HtmlTag } from "svelte/internal";

    export let columns;
    let locale = "de";

    $: dayjs.locale(locale);
    async function updateRepo(buchungstag, id) {
        const res = await fetch("http://localhost:8081/abrechnung/updateTag", {
            method: "POST",
            body: JSON.stringify({
                buchungstag: buchungstag,
                id: id,
            }),
            headers: {
                "content-type": "application/json",
            },
        });
    }

    function handleItemFinalize(columnIdx, newItems) {
        columns[columnIdx].buchungsblocks = newItems;
        updateRepo(columns[columnIdx].buchungstag, newItems[0].id);
    }
</script>

<div class="ui center aligned three column grid container">
    <div class="row">
        <div class="right floated middle aligned column">
        <h2 class="ui header">Abrechnung</h2>
        </div>
        <div class="column">
            <button on:click={prevWeek}>&lt;</button>
            <Datepicker value={{ locale }} />
            <button on:click={nextWeek}>&gt;</button>
        </div>
        <div class="column"></div>
    </div>
    <div class="row">
        <div class="column">
            <button on:click={nextWeek}>Heute</button>
        </div>
    </div>
</div>
<div class="ui seven column equal width stackable grid container internally celled">
    <div class="row">
        {#each columns as { buchungstag, tag, buchungsblocks }, idx (buchungstag)}
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
