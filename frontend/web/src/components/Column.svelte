<script>
    import { flip } from "svelte/animate";
    import { dndzone } from "svelte-dnd-action";
    import { store } from "../stores/store";
    import AutoComplete from "simple-svelte-autocomplete";
    import { onMount } from "svelte";

    export let tag;
    export let buchungstag;
    export let buchungsblocks;
    export let colIdx;
    export let onDrop;

    let can_saving = true;
    let errors = {};
    let name = "";
    let zeit = "";
    let vorname = "";
    let geburtsdatum = "";
    let adresses = [];
    let strasse = "";
    let selectedStrasse;
    let selectedName;
    let itemName;
    let plz = "";
    let ort = "";
    let hausbesuch = 1;
    let gebuchteZeit = 0;
    let abwesend = 1;

    let startAdresse;
    let startPlz;
    let startOrt;
    let count = 0;
    let zielAdresse;
    let zielPlz;
    let zielOrt;

    let selectedItemId = null;
    let selectedIndex;
    let showFahrtweg = false;
    let showAbwesenheit = false;
    let showEditor = false;
    let showEditorFahrt = false;
    let showEditorAbwesenheit = false;
    let isPatientSelected = false;
    let questions = [
        { id: 1, text: `Urlaubstag` },
        { id: 2, text: `1/2 Urlaubstag` },
        { id: 3, text: `Krank` },
    ];

    let selected;

    let answer = "";

    const flipDurationMs = 150;

    onMount(async () => {     
        window.$('.ui.radio.checkbox').checkbox();
        updateProgress();
    });

    function showEditButton(id)
    {
        window.$("."+id).transition();
    }

    function handleDndConsiderCards(e) {
        buchungsblocks = e.detail.items;
        updateProgress();
    }

    async function handleDndFinalizeCards(e) {
        onDrop(e.detail.items);
    }

    function convertDate(input) {
        return new Date(input).toLocaleDateString("de-DE", {
            // you can use undefined as first argument
            year: "numeric",
            month: "2-digit",
            day: "2-digit",
        });
    }

    function handleErrors(response) {
        if (!response.ok) {
            throw Error(response.text());
        }
        return response;
    }

    async function addCard(typ) {
        if (typ == "patient") {
            can_saving = false;
            const payload = JSON.stringify({
                id: showEditor ? selectedItemId : null,
                buchungstag: buchungstag,
                gebucht: zeit,
                reihenfolge: buchungsblocks.length,
                therapeut: "admin",
                patient_name: name,
                patient_vorname: vorname,
                patient_geburtstag: geburtsdatum,
                patient_strasse: strasse,
                patient_ort: ort,
                patient_plz: plz,
                hausbesuch: hausbesuch
            });

            errors = {};
            const json = await fetch(
                "http://localhost:8081/abrechnung/save/therapie",
                {
                    method: "POST",
                    body: payload,
                    headers: {
                        Authorization: "Basic " + btoa("admin:password"),
                        "content-type": "application/json",
                    },
                }
            ).then((response) => {
                can_saving = true;
                return response.json();
            });
            {
                if (json.hasOwnProperty("error")) {
                    errors = json;
                    can_saving = true;
                    return;
                }
            }
            if(showEditor)
            {
                buchungsblocks = buchungsblocks.filter(e => e.parentId !== selectedItemId)
            }
            buchungsblocks = [...buchungsblocks, json];
            buchungsblocks.sort(function(a, b){return a.reihenfolge - b.reihenfolge});
            reset();
            window.$(".ui.modal.new-therapie-" + tag).modal("hide");
            updateProgress();
        }
        if(typ == "abwesend")
        {
            const payload = JSON.stringify({
                buchungstag: buchungstag,
                reihenfolge: buchungsblocks.length,
                abwesenheit: abwesend
            });
            const json = await fetch(
                "http://localhost:8081/abrechnung/save/abwesend",
                {
                    method: "POST",
                    body: payload,
                    headers: {
                        Authorization: "Basic " + btoa("admin:password"),
                        "content-type": "application/json",
                    },
                }
            ).then((response) => {
                can_saving = true;
                return response.json();
            });
            buchungsblocks = [...buchungsblocks, json];
            buchungsblocks.sort(function(a, b){return a.reihenfolge - b.reihenfolge});
            window.$(".ui.modal.new-abwesenheit-" + tag).modal("hide");
        }
    }

    function showEdit(item) {
        if (item.buchungstyp === "THERAPIE") {
            showEditor = true;
            selectedItemId = item.parentId;
            loadBuchung(item.parentId)
            showModalTherapie();
            return;
        }
        if (item.typ === "fahrtweg") {
            showEditorFahrt = true;
            return;
        }
        if (item.typ === "abwesenheit") {
            showEditorAbwesenheit = true;
            return;
        }
    }

    async function loadBuchung(id) {
        const url =
            "http://localhost:8081/abrechnung/therapie/" +
            encodeURIComponent(id);

        const response = await fetch(url, {
            method: "GET",
            headers: {
                Authorization: "Basic " + btoa("admin:password"),
                "content-type": "application/json",
            },
        });
        var result = await response.json();
        buchungstag = result.buchungstag;
        zeit = result.gebuchteZeit;
        vorname = result.vorname;
        name = result.name;
        geburtsdatum = result.geburtsdatum;
        strasse = result.strasse;
        plz = result.plz;
        ort = result.ort;
        hausbesuch = result.hausbesuch;
    }

    function updateProgress() {
        gebuchteZeit = 0;
        count = 0;
        buchungsblocks.forEach((item) => {
            count += item.dauer;
        });
        gebuchteZeit = parseInt((count / 8) * 100, 10);
        window.$("#progress-" + tag).progress({
            percent: gebuchteZeit,
            showActivity: false
        });
    }

    function updateCard() {
        $store[colIdx].items[selectedIndex].name = name;
        $store[colIdx].items[selectedIndex].zeit = zeit;
        name = "";
        zeit = "";
        showEditor = false;
    }
    async function deleteCard() {
        if(showEditor)
        {
            const url =
            "http://localhost:8081/abrechnung/delete/therapie/" +
            encodeURIComponent(selectedItemId);
            const response = await fetch(url, {
            method: "GET",
            headers: {
                Authorization: "Basic " + btoa("admin:password"),
                "content-type": "application/json",
            },
        });
        buchungsblocks = buchungsblocks.filter(e => e.parentId !== selectedItemId)
        } 
        showEditor = false;
    }

    async function getName(keyword) {
        const url =
            "http://localhost:8081/abrechnung/name/" +
            encodeURIComponent(keyword);

        const response = await fetch(url, {
            method: "GET",
            headers: {
                Authorization: "Basic " + btoa("admin:password"),
                "content-type": "application/json",
            },
        });
        return await response.json();
    }

    function reset() {
        change(null);
    }

    function change(item) {
        if (item != null) {
            name = item.name;
            vorname = item.vorname;
            geburtsdatum = convertDate(item.geburtstag);
            adresses = item.adresses;
            selectedStrasse = adresses[0];
            isPatientSelected = true;
        }
    }

    function changeAdress(item) {
        if (item != null) {
            strasse = item.strasse;

            plz = item.ort.plz;
            ort = item.ort.ort;
        }
    }
    function showModalTherapie() {
        window.$(".ui.modal.new-therapie-" + tag)
            .modal({
                blurring: true,
                transition: "vertical flip",
                onApprove: function () {
                    return false;
                },
            })
            .modal("show");

        window.$(".info-zeit").popup({
            title: "Zeit eintragen",
            content: "Die Zeit kann als 2h oder 2 oder 0.5 = 30min",
        });
    }
    function showModalAbwesenheit() {
        window.$(".ui.modal.new-abwesenheit-" + tag)
            .modal({
                blurring: true,
                transition: "vertical flip",
                onApprove: function () {
                    return false;
                },
            })
            .modal("show");
    }
    function handleCreatePatient(patient) {
        name = patient;
        item = {
            name: patient,
            vorname: "Leer",
            geburtstag: "01.01.1970",
        };
        return item;
    }
    function setNameText(text)
    {
        name = text
        return name
    }

    function setAdresseText(newStrasse)
    {
        strasse = newStrasse
        return strasse
    }

    function handleCreateAdresse(newStrasse) {
        strasse = newStrasse;
        item = {
            strasse: newStrasse,
            ort: {
                plz: "00000",
                ort: "Ort",
            },
        };
        return item;
    }
</script>

<div>
    <p>{tag}<br />{convertDate(buchungstag)}</p>
</div>
<div>
    <div>
        <div class="ui teal progress" class:error={count > 11} id="progress-{tag}">
            <div class="bar" />
            <div class="label">{parseInt(count)}h von 8h</div>
        </div>

        <div class="ui section divider" />
        <div class="ui vertical labeled icon buttons container">
            <button
                class="ui button"
                data-tooltip="Neue Therapie eintragen"
                on:click={() => {showEditor = false; showModalTherapie();}}
                ><i class="tasks icon" />Therapie</button
            >
            <button
                class="ui button"
                data-tooltip="Neue Abwesenheit eintragen"
                on:click={() =>  {showEditor = false; showModalAbwesenheit();}}
                ><i class="coffee icon" />Abwesend</button
            >
        </div>
        <div class="ui section divider" />
    </div>
</div>
<div
    style="min-height: 2em;"
    use:dndzone={{
        items: buchungsblocks,
        flipDurationMs,
        zoneTabIndex: -1,
        dropTargetStyle: {
            outline: "rgba(0, 255, 196, 0.7) solid 2px",
        },
    }}
    on:consider={handleDndConsiderCards}
    on:finalize={handleDndFinalizeCards}
>
    {#each buchungsblocks as item, i (item.id)}
        <div class="ui card buchungsblock" animate:flip={{ duration: flipDurationMs }}>          
            <div class="ui content" on:mouseenter={showEditButton(item.id)} on:mouseleave={showEditButton(item.id)}>
                <button
                    class="ui floating small circular compact icon button label hidden {item.id}"
                    on:click={showEdit(item)}><i class="cog icon" /></button
                >
                {@html item.titel}   
                {#if item.hausbesuch > 1}    
                    <div class="ui right floated meta">
                    <i class="home icon teal" />    
                {#if item.hausbesuch > 2}
                <i class="car icon teal" />      
            {/if}     
                </div>
            {/if}                
            </div>
        </div>
    {/each}
</div>

<div class="ui modal new-therapie-{tag}">
    <i class="close icon" />
    {#if showEditor}
    <div class="header">Bearbeite Therapie</div>
    {:else}
    <div class="header">Neue Therapie anlegen für {tag}</div>
    {/if}
    <div class="content">
        <form
            class="ui form"
        >
            <div class="field">
                <h5>
                    Therapieeinheit
                    <i class="info circle icon link info-zeit" />
                </h5>
                <div class="field" class:error={errors.gebucht}>
                    <input bind:value={zeit} placeholder="Dauer" />
                    {#if errors.gebucht}
                        <div class="ui pointing red basic label">
                            {errors.gebucht}
                        </div>
                    {/if}
                </div>
            </div>
            <div class="field">
                <h5>Patienten Daten</h5>
                <div class="three fields">
                    <div
                        class="six wide field"
                        class:error={errors.patient_name}
                    >
                    {#if showEditor}
                    <input bind:value={name} placeholder="Name" />
                    {:else}
                        <AutoComplete
                            hideArrow="true"
                            html5autocomplete="true"
                            inputClassName="auto-input"
                            placeholder="Name"
                            searchFunction={getName}
                            textCleanFunction={setNameText}
                            minCharactersToSearch="3"
                            delay="200"
                            onChange={change}
                            labelFieldName="name"
                            valueFieldName="id"
                            create={true}
                            createText={"Patient gibt es noch nicht, neu anlegen?"}
                            onCreate={handleCreatePatient}
                        >
                            <div slot="item" let:item>
                                <div>{item.name}, {item.vorname}</div>
                                {convertDate(item.geburtstag)}
                            </div>
                        </AutoComplete>
                        {/if}
                        {#if errors.patient_name}
                            <div class="ui pointing red basic label">
                                {errors.patient_name}
                            </div>
                        {/if}
                    </div>
                    <div
                        class="six wide field"
                        class:error={errors.patient_vorname}
                    >
                        <input bind:value={vorname} placeholder="Vorname" />
                        {#if errors.patient_vorname}
                            <div class="ui pointing red basic label">
                                {errors.patient_vorname}
                            </div>
                        {/if}
                    </div>
                    <div
                        class="four wide field"
                        class:error={errors.patient_geburtstag}
                    >
                        <input
                            bind:value={geburtsdatum}
                            placeholder="Geburtstag"
                        />
                        {#if errors.patient_geburtstag}
                            <div class="ui pointing red basic label">
                                {errors.patient_geburtstag}
                            </div>
                        {/if}
                    </div>
                </div>
            </div>
            <div class="field">
                <h5>Adresse</h5>
                <div class="three fields"
                class:error={errors.patient_strasse}>
                    <div class="nine wide field">
                        {#if showEditor}
                            <input bind:value={strasse} placeholder="Adresse" />
                        {:else}
                        <AutoComplete
                            bind:selectedItem={selectedStrasse}
                            items={adresses}
                            inputClassName="auto-input"
                            textCleanFunction={setAdresseText}
                            placeholder="Adresse"
                            labelFieldName="strasse"
                            onChange={changeAdress}
                            create={true}
                            createText={"Adresse gibt es noch nicht, neu anlegen?"}
                            onCreate={handleCreateAdresse}
                        >
                            <div slot="item" let:item>
                                <div>
                                    {item.strasse}, {item.ort.plz}
                                    {item.ort.ort}
                                </div>
                            </div>
                        </AutoComplete>
                        {/if}
                        {#if errors.patient_strasse}
                        <div class="ui pointing red basic label">
                            {errors.patient_strasse}
                        </div>
                        {/if}
                    </div>
                    <div
                        class="two wide field"
                        class:error={errors.patient_plz}
                    >
                        <input bind:value={plz} placeholder="PLZ" />
                        {#if errors.patient_plz}
                            <div class="ui pointing red basic label">
                                {errors.patient_plz}
                            </div>
                        {/if}
                    </div>
                    <div
                        class="five wide field"
                        class:error={errors.patient_ort}
                    >
                        <input bind:value={ort} placeholder="Ort" />
                        {#if errors.patient_ort}
                            <div class="ui pointing red basic label">
                                {errors.patient_ort}
                            </div>
                        {/if}
                    </div>
                </div>
            </div>
            <h4 class="ui dividing header">Hausbesuch</h4>           
            <div class="grouped fields">
                <div class="field">
                  <div class="ui radio checkbox">
                    <input type=radio bind:group={hausbesuch} name="hausbesuch" value={1}>
                    <label>Praxis</label>
                  </div>
                </div>
                <div class="field">
                  <div class="ui radio checkbox">
                    <input type=radio bind:group={hausbesuch} name="hausbesuch" value={2}>
                    <label>Hausbesuch</label>
                  </div>
                </div>
                <div class="field">
                  <div class="ui radio checkbox">
                    <input type=radio bind:group={hausbesuch} name="hausbesuch" value={3}>
                    <label>Hausbesuch mit Praxisauto</label>
                  </div>
                </div>
              </div>
            <div class="ui clearing divider" />
            {#if can_saving}
            <button class="ui positive right labeled icon button" on:click={ ()=>addCard("patient")}>
                Speichern
                <i class="checkmark icon" />
            </button>
           {:else}
           <button class="ui disabled positive right labeled icon button">
            Speichern
            <i class="checkmark icon" />
           </button>
	       {/if}
           {#if showEditor}
           <button class="ui button negative" on:click={ ()=>{window.$(".ui.modal.new-therapie-" + tag).modal("hide");deleteCard()} }>Löschen</button>
           {/if}
           <button class="ui button" on:click={ ()=>window.$(".ui.modal.new-therapie-" + tag).modal("hide")}>Abbrechen</button>
        </form>
    </div>
</div>

<div class="ui modal new-abwesenheit-{tag}">
    <i class="close icon" />
    {#if showEditor}
    <div class="header">Bearbeite Therapie</div>
    {:else}
    <div class="header">Neue Abwesenehit anlegen für {tag}</div>
    {/if}
    <div class="content">
        <form
            class="ui form"
        >
        <div class="grouped fields">
            <div class="field">
              <div class="ui radio checkbox">
                <input type=radio bind:group={abwesend} name="abwesend" value={1}>
                <label>Urlaubstag</label>
              </div>
            </div>
            <div class="field">
              <div class="ui radio checkbox">
                <input type=radio bind:group={abwesend} name="abwesend" value={2}>
                <label>1/2 Urlaubstag</label>
              </div>
            </div>
            <div class="field">
              <div class="ui radio checkbox">
                <input type=radio bind:group={abwesend} name="abwesend" value={3}>
                <label>Andere</label>
              </div>
            </div>
          </div>
        <div class="ui clearing divider" />
        {#if can_saving}
        <button class="ui positive right labeled icon button" on:click={ ()=>addCard("abwesend")}>
            Speichern
            <i class="checkmark icon" />
        </button>
       {:else}
       <button class="ui disabled positive right labeled icon button">
        Speichern
        <i class="checkmark icon" />
       </button>
       {/if}
       {#if showEditor}
       <button class="ui button negative" on:click={ ()=>{window.$(".ui.modal.new-abwesenheit-" + tag).modal("hide");deleteCard()} }>Löschen</button>
       {/if}
       <button class="ui button" on:click={ ()=>window.$(".ui.modal.new-abwesenheit-" + tag).modal("hide")}>Abbrechen</button>
        </form>
    </div>
</div>
