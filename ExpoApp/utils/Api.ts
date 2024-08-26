import {getAuthenticationKey, getRoom} from "./Storage";
import { encode } from 'base-64';
import {AppContext} from "../components/AppContex";

export async function makeApiCall(appContext: AppContext, url: string, options?: {
    method?: string,
    auth?: string}) {
    let auth = options?.auth;
    if (!auth) {
        auth = getAuthenticationKey();
    }
    auth = encode("webapp:" + auth);

    try {
        const response = await fetch("https://home.henrik.org/remote/" + url, {
            headers: {
                Authorization: `Basic ${auth}`,
            },
            method: options?.method || "GET"
        });

        if (!response.ok) {
            appContext.setNotification("Encountered networking issues");
        }
        return response;
    } catch (e) {
        appContext.setNotification("Encountered networking issues");
        throw e;
    }
}

export async function currentRoomPage(appContext: AppContext, room?: string) {
    const response = await makeApiCall(appContext, "room/" + (room ?? getRoom()));
    try {
        return parseInt(await response.text());
    } catch (e) {
        return 0;
    }
}

export async function activeQueue(appContext: AppContext, room?: string) {
    const response = await makeApiCall(appContext, "activequeue/" + (room ?? getRoom()));
    try {
        return await response.text();
    } catch (e) {
        return "Tv";
    }
}

export async function setRoomPage(appContext: AppContext, page: number, room?: string) {
    try {
        await makeApiCall(appContext, "room/" + (room ?? getRoom()) + "/" + page, {
            method: "PUT"
        });
    } catch (e) {
    }
}

let buttonsEnabled = true;

export function disableButtons() {
    buttonsEnabled = false;
}

export function enableButtons() {
    buttonsEnabled = true;
}

export async function apiSend(appContext: AppContext, action: string) {
    if (buttonsEnabled) {
        try {
            await makeApiCall(appContext, "send/" + getRoom() + "/" + action);
        } catch (e) {
        }
    }
}

export async function apiIdle(appContext: AppContext, action: string) {
    if (buttonsEnabled) {
        try {
            await makeApiCall(appContext, "idle/" + getRoom() + "/" + action);
        } catch (e) {
        }
    }
}

export interface MediaItem {
    itemId : string;
    artist: string;
    album?: string;
    title?: string;
    duration? : number;
    played?: number;
    voted?: number;
    trackNumber?: number;
    rating?: number;
    toplist?: number;
    coverUrl?: string;
}

export interface QueueItem extends MediaItem {
    queueId: number;
}

export async function queueContents(appContext: AppContext, queue: string) : Promise<QueueItem[]>{
    try {
        const response = await makeApiCall(appContext, "queue/" + getRoom() + "/" + queue);

        return await response.json();
    } catch (e) {
        return [];
    }
}

export async function query(appContext: AppContext,
                            queue: string,
                            type: string,
                            criteria: string,
                            offset: number, size: number) : Promise<MediaItem[]> {
    try {
        const response = await makeApiCall(appContext, "search/" + queue + "/" + type.replace(" ", "") + "/"
            + encodeURIComponent(criteria) + "?offset=" + offset + "&size=" + size);

        return await response.json();
    } catch (e) {
        return [];
    }
}