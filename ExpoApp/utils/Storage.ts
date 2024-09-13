import {MMKV} from 'react-native-mmkv'

let authenticated = false;

export const storage = new MMKV()

export function getRoom() {
    return storage.getString("room");
}

export async function initializeStorage() {
}

export function setExpanded(value: boolean) {
    storage.set("expanded", value);
}

export function getExpanded() {
    return !!storage.getBoolean("expanded");
}

export function setRoom(value: string) {
    storage.set("room", value);
}

export function getAuthenticationKey() {
    return storage.getString("auth");
}

export function setAuthenticationKey(value: string) {
    storage.set("auth", value);
}

export function getAuthenticated() {
    return authenticated;
}

export function setAuthenticated(value: boolean) {
    authenticated = value;
}