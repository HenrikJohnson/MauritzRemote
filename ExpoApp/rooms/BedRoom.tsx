import {createMaterialTopTabNavigator} from "@react-navigation/material-top-tabs";
import CableIcon from "../assets/icons/cable.svg";
import {RoomNavigator} from "../components/RoomNavigator";
import {CableScreen} from "../screens/CableScreen";
import {apiSend} from "../utils/Api";
import {PowerVolume} from "../components/PowerVolume";
import React from "react";
import {useAppContent} from "../components/AppContex";

const Tab = createMaterialTopTabNavigator();

export function BedRoom() {
    const keyboard = useAppContent();

    return <>
        <RoomNavigator screens={["Roku"]} room={"Bedroom"} onTabPress={(index) => {
            apiSend(keyboard, `Turn_Cable_On`);
        }}>
            <Tab.Screen name="Roku" component={CableScreen} options={{
                tabBarIcon: (props) => <CableIcon width={32} height={32} fill={props.color}/>
            }}/>
        </RoomNavigator>
        <PowerVolume/>
    </>
}
