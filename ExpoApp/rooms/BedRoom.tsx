import {createMaterialTopTabNavigator} from "@react-navigation/material-top-tabs";
import CableIcon from "../assets/icons/cable.svg";
import {RoomNavigator} from "../components/RoomNavigator";
import {CableScreen} from "../screens/CableScreen";
import {apiSend} from "../utils/Api";
import {PowerVolume} from "../components/PowerVolume";
import React, {useEffect, useState} from "react";
import {getScreenHeight, getScreenWidth} from "../utils/Layout";
import {useAppContent} from "../components/AppContex";
import {Dimensions} from "react-native";

const Tab = createMaterialTopTabNavigator();

export function BedRoom() {
    const keyboard = useAppContent();

    return <>
        <RoomNavigator screens={["Roku"]} room={"Cable"} onTabPress={(index) => {
            apiSend(keyboard, `Turn_Cable_On`);
        }}>
            <Tab.Screen name="Roku" component={CableScreen} options={{
                tabBarIcon: (props) => <CableIcon width={32} height={32} fill={props.color}/>
            }}/>
        </RoomNavigator>
        <PowerVolume/>
    </>
}
