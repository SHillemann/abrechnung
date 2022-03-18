<script>
    import {fly} from 'svelte/transition'
  
    export let card = {}
  
    const handleDragStart = e => e.dataTransfer.setData('text', JSON.stringify(card))
  </script>
  
  <div
    class="card"
    draggable="true"
    on:dragstart={handleDragStart}
    in:fly="{{y: 20, duration: 500}}"
  >
    <div class="card-content">
        {card.description}
    </div>
  </div>
  
  <style>
    .card {
      margin-bottom: 20px;
      border-radius: 5px;
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
      transition: box-shadow .3s ease-in-out;
    }
  
    .card:hover {
      box-shadow: 0 7px 12px rgba(0, 0, 0, 0.2);
    }
  </style>