<script>
    import Card from './Card.svelte'
    import {cards} from '../store/cards'
  
    export let type = ''
  
    const getType = () => type.replace(/-/g, ' ')
  
    let addingCard = false
    let description = ''
    let typeCards = []
    cards.subscribe(cards => typeCards = cards[type] || [])
  
    const addCard = () => {
      if (description) {
        const newCard = {description, type, id: Math.random()}
        cards.update(cards => {
          const newCards = {...cards, [type]: cards[type] ? [...cards[type], newCard] : [newCard]}
          localStorage.setItem('cards', JSON.stringify(newCards))
          return newCards
        })
      }
      addingCard = false
      description = ''
    }
  
    const moveCard = card => {
      cards.update(cards => {
        const previousType = card.type
        const updatedCard = {...card, type}
        const previousTypeCards = cards[previousType].filter(item => item.id !== card.id)
        const currentTypeCards = cards[type] ? [...cards[type], updatedCard] : [updatedCard]
        const newCards = {...cards, [previousType]: previousTypeCards, [type]: currentTypeCards}
        localStorage.setItem('cards', JSON.stringify(newCards))
        return newCards
      })
    }
  
    const handleDragOver = e =>  e.preventDefault()
  
    const handleDragDrop = e => {
      e.preventDefault()
      const card = e.dataTransfer.getData('text') ? JSON.parse(e.dataTransfer.getData('text')) : null
      if (card && card.type !== type) moveCard(card)
    }
  </script>
  
  <div class="column">
    <article class="message is-dark">
      <div class="message-header">
        <p class="is-capitalized">{getType()}</p>
      </div>
      <div class="message-body" on:dragover={handleDragOver} on:drop={handleDragDrop}>
        {#if typeCards.length}
          {#each typeCards as card}
            <Card card="{card}" />
          {/each}
        {:else}
          <p class="has-text-centered">There are no cards</p>
        {/if}
          {#if addingCard}
            <textarea class="textarea" bind:value={description}></textarea>
          {/if}
        <div class="navbar-end">
            {#if addingCard}
              <button class="button" on:click={addCard} style="margin-top: 20px">Add card</button>
            {:else}
              <button class="button" on:click={() => addingCard = true}>Add another card</button>
            {/if}
        </div>
      </div>
    </article>
  </div>