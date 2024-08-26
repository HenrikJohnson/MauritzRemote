import {MMKV} from "react-native-mmkv";

const storage = new MMKV();
let authenticated = false;

export function getRoom() {
  const room = storage.getString("room");
  if (room) {
    return room;
  }
  return "Livingroom";
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