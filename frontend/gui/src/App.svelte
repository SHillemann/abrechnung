<script>
	import Board from "./components/Board.svelte";
	import { store } from "./stores/store";
</script>

<Board columns={$store}/>
