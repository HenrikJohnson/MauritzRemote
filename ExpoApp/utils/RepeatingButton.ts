import {apiIdle, apiSend} from "./Api";
import {AppContext} from "../components/AppContex";

const INITIAL_DELAY = 100;
const SECONDARY_DELAY = 300;
const REPEAT_INTERVAL = 50;

let repeatingAction : string | undefined = undefined;
let nextActionId = 0;
let currentTimerId = -1;
let currentIntervalId = -1;

function stopSending() {
    if (currentTimerId >= 0)
        clearTimeout(currentTimerId);
    if (currentIntervalId >= 0)
        clearInterval(currentIntervalId);

    currentTimerId = -1;
    currentIntervalId = -1;
    nextActionId++;
    repeatingAction = undefined;
}

export function startSendingAction(appContext: AppContext, action: string) {
    console.log("startSendingAction", action);
    stopSending();
    let currentActionId = ++nextActionId;
    repeatingAction = action;

    setTimeout(() => {
        apiSend(appContext, action);

        if (nextActionId === currentActionId && currentTimerId < 0) {
            currentTimerId = setTimeout(() => {
                if (nextActionId === currentActionId && currentIntervalId < 0) {
                    currentIntervalId = setInterval(() => {
                        if (nextActionId === currentActionId) {
                            apiIdle(appContext, action);
                        }
                    }, REPEAT_INTERVAL) as unknown as number;
                }
            }, SECONDARY_DELAY) as unknown as number;
        }
    }, INITIAL_DELAY);
}

export function stopSendingAction(appContext: AppContext, action: string) {
    console.log("stopSendingAction", action);
    if (repeatingAction === action) {
        stopSending();
    }
}