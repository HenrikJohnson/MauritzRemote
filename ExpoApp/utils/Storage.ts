import AsyncStorage from "@react-native-async-storage/async-storage";

let authenticated = false;
let room = "Livingroom";
let auth = undefined as string | undefined;

export function getRoom() {
    return room;
}

export async function initializeStorage() {
    room = await AsyncStorage.getItem("room") ?? "Livingroom";
    auth = await AsyncStorage.getItem("auth") ?? undefined;
}

export function setRoom(value: string) {
    room = value;
    AsyncStorage.setItem("room", value);
}

export function getAuthenticationKey() {
    return auth;
}

export function setAuthenticationKey(value: string) {
    auth = value;
    AsyncStorage.setItem("auth", value);
}

export function getAuthenticated() {
    return authenticated;
}

export function setAuthenticated(value: boolean) {
    authenticated = value;
}