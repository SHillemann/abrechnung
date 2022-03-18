import {writable} from 'svelte/store'

export const cards = writable(localStorage.getItem('cards') ? JSON.parse(localStorage.getItem('cards')) : {})
