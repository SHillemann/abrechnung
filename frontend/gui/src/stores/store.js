import { writable } from 'svelte/store';

let offset = 0;

export function initialValue() {
        return [
            {
                tag: "Montag",
                buchungstag: "01.04.2022",
                buchungsblocks: []
            },
            {
                tag: "Dienstag",
                buchungstag: "02.04.2022",
                buchungsblocks: []
            },
            {
                tag: "Mittwoch",
                buchungstag: "03.04.2022",
                buchungsblocks: []
            },
            {
                tag: "Donnerstag",
                buchungstag: "04.04.2022",
                buchungsblocks: []
            },
            {
                tag: "Freitag",
                buchungstag: "05.04.2022",
                buchungsblocks: []
            },
            {
                tag: "Samstag",
                buchungstag: "06.04.2022",
                buchungsblocks: []
            },
            {
                tag: "Sonntag",
                buchungstag: "07.04.2022",
                buchungsblocks: []
            },
        ]
    }

export const store = makeUserStore("args");

export function makeUserStore(args) {
	// 1. Build the store and initialize it as empty and error free
	let initial = initialValue();
	let store = writable(initial, makeSubscribe(initial, args)); 
	return store;
}

function unsubscribe() {
	// Nothing to do in this case
}

export function nextWeek()
{
    offset = ++offset;
    updateWeek();
}

export function prevWeek()
{
    offset = --offset;
    updateWeek();
}


async function updateWeek()
{
    try {
		// 5. Dispatch the request for the users
      const response = await fetch("http://localhost:8081/abrechnung/week", {
        method: "POST",
        body: JSON.stringify({
            name: "admin",
            weekOffset: offset,
        }),
        headers: {
            'Authorization': 'Basic ' + btoa('admin:password'),
            "content-type": "application/json",
        },
    });	

		if(response.ok) {
  	  const users = await response.json();
        store.set(users);
			
		} else {
			const text = response.text();
			throw new Error(text);
		}
		
	} catch(error) {
		// 6b. if there is a fetch error - deal with it
		// and let observers know
		store.set(error);
	}
}

function makeSubscribe(data, _args) {
	// 2. Create a closure with access to the 
	// initial data and initialization arguments
	return set => {
		// 3. This won't get executed until the store has 
		// its first subscriber. Kick off retrieval.
		fetchUserData(data, set);
		
		// 4. We're not waiting for the response.
		// Return the unsubscribe function which doesn't do
		// do anything here (but is part of the stores protocol).
		return unsubscribe;
	};
}

async function fetchUserData(data, set) {
	try {
		// 5. Dispatch the request for the users
      const response = await fetch("http://localhost:8081/abrechnung/week", {
        method: "POST",
        body: JSON.stringify({
            name: "admin",
            weekOffset: 0,
        }),
        headers: {
            'Authorization': 'Basic ' + btoa('admin:password'),
            "content-type": "application/json",
        },
    });	

		if(response.ok) {
  	  const users = await response.json();
        set(users);
			
		} else {
			const text = response.text();
			throw new Error(text);
		}
		
	} catch(error) {
		// 6b. if there is a fetch error - deal with it
		// and let observers know
		data.error = error;
		set(data);
	}
}
