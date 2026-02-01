import {apiIdle, apiSend} from "./Api";
import {AppContext} from "../components/AppContex";

const INITIAL_DELAY = 100;
const SECONDARY_DELAY = 300;
const REPEAT_INTERVAL = 50;

let repeatingAction: string | undefined = undefined;
let currentActionId = 0;
let initialTimerId: ReturnType<typeof setTimeout> | null = null;
let secondaryTimerId: ReturnType<typeof setTimeout> | null = null;
let intervalId: ReturnType<typeof setInterval> | null = null;

function stopSending() {
    if (initialTimerId !== null) {
        clearTimeout(initialTimerId);
        initialTimerId = null;
    }
    if (secondaryTimerId !== null) {
        clearTimeout(secondaryTimerId);
        secondaryTimerId = null;
    }
    if (intervalId !== null) {
        clearInterval(intervalId);
        intervalId = null;
    }
    currentActionId++;
    repeatingAction = undefined;
}

export function startSendingAction(appContext: AppContext, action: string) {
    console.log("startSendingAction", action);
    stopSending();

    const thisActionId = ++currentActionId;
    repeatingAction = action;

    initialTimerId = setTimeout(() => {
        initialTimerId = null;

        // Guard: don't send if we're no longer the active action
        if (currentActionId !== thisActionId) return;

        apiSend(appContext, action);

        secondaryTimerId = setTimeout(() => {
            secondaryTimerId = null;
            if (currentActionId !== thisActionId) return;

            intervalId = setInterval(() => {
                if (currentActionId === thisActionId) {
                    apiIdle(appContext, action);
                }
            }, REPEAT_INTERVAL);
        }, SECONDARY_DELAY);
    }, INITIAL_DELAY);
}

export function stopSendingAction(appContext: AppContext, action: string) {
    // Keep the guard - it correctly handles overlapping buttons
    // (releasing an old button shouldn't stop the currently active one)
    if (repeatingAction === action) {
        console.log("stopSendingAction", action);
        stopSending();
    }
}
