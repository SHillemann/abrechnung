<script>
	import Header from './components/Header.svelte'
	import Column from './components/Column.svelte'
  </script>
  
  <Header/>
  <div class="container">
	<div class="columns">
	  <Column type="to-do" />
	  <Column type="in-progress" />
	  <Column type="done" />
	</div>
  </div>