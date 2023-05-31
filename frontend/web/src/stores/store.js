import { writable } from 'svelte/store';



export function initialValue() {
        return [
            {
                tag: "Keine Verbindung",
                buchungstag: "01.01.2000",
                buchungsblocks: []
            },
            {
                tag: "Keine Verbindung",
                buchungstag: "02.01.2000",
                buchungsblocks: []
            },
            {
                tag: "Keine Verbindung",
                buchungstag: "03.01.2000",
                buchungsblocks: []
            },
            {
                tag: "Keine Verbindung",
                buchungstag: "04.01.2000",
                buchungsblocks: []
            },
            {
                tag: "Keine Verbindung",
                buchungstag: "05.01.2000",
                buchungsblocks: []
            },
            {
                tag: "Keine Verbindung",
                buchungstag: "06.01.2000",
                buchungsblocks: []
            },
            {
                tag: "Keine Verbindung",
                buchungstag: "07.01.2000",
                buchungsblocks: []
            },
        ]
    }

export const store = makeUserStore("args");
export const offsetDate = writable(0);
export function makeUserStore(args) {
	// 1. Build the store and initialize it as empty and error free
	let initial = initialValue();
	let store = writable(initial, makeSubscribe(initial, args)); 
	return store;
}

function unsubscribe() {
	// Nothing to do in this case
}


export async function updateDate(date)
{   
    if (date !== undefined) {
    try {
		// 5. Dispatch the request for the users
      const response = await fetch("http://localhost:8081/abrechnung/day", {
        method: "POST",
        body: JSON.stringify({
            name: "admin",
            date: date.getTime(),
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
      const response = await fetch("http://localhost:8081/abrechnung/day", {
        method: "POST",
        body: JSON.stringify({
            name: "admin",
            date: new Date().getTime(),
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
