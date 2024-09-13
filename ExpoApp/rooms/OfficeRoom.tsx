import {createMaterialTopTabNavigator} from "@react-navigation/material-top-tabs";
import MediaCenterIcon from "../assets/icons/mediacenter.svg";
import CableIcon from "../assets/icons/cable.svg";
import SwedishIcon from "../assets/icons/swedish.svg";
import GameIcon from "../assets/icons/game.svg";
import {RoomNavigator} from "../components/RoomNavigator";
import {CableScreen} from "../screens/CableScreen";
import {apiSend} from "../utils/Api";
import {SwedishScreen} from "../screens/SwedishScreen";
import React from "react";
import {PowerVolume} from "../components/PowerVolume";
import {GameScreen} from "../screens/GameScreen";
import {OfficeMediaCenterScreen, Zone2MediaCenterScreen} from "../screens/MediaCenterScreen";
import {getScreenHeight, getScreenWidth} from "../utils/Layout";
import {useAppContent} from "../components/AppContex";

const Tab = createMaterialTopTabNavigator();

export function OfficeRoom(props: { room?: string, mediaComponent?: React.ComponentType }) {
    const keyboard = useAppContent();
    return <>
        <RoomNavigator screens={["Media", "Roku", "Swedish", "Games"]} room={props.room ?? "Office"}
                       onTabPress={(index) => {
                           let action = "Media_Center";
                           switch (index) {
                               case 1:
                                   action = "Cable";
                                   break;
                               case 2:
                                   action = "AmazonTV";
                                   break;
                               case 3:
                                   action = "Game";
                                   break;
                           }
                           apiSend(keyboard, `Turn_${action}_On`);
                       }}>
            <Tab.Screen name="Media" component={props.mediaComponent ?? OfficeMediaCenterScreen} options={{
                tabBarIcon: (props) => <MediaCenterIcon width={32} height={32} fill={props.color}/>
            }}/>
            <Tab.Screen name="Roku" component={CableScreen} options={{
                tabBarIcon: (props) => <CableIcon width={32} height={32} fill={props.color}/>
            }}/>
            <Tab.Screen name="Swedish" component={SwedishScreen} options={{
                tabBarIcon: (props) => <SwedishIcon width={32} height={32} fill={props.color}/>
            }}/>
            <Tab.Screen name="Games" component={GameScreen} options={{
                tabBarIcon: (props) => <GameIcon width={32} height={32} fill={props.color}/>
            }}/>
        </RoomNavigator>
        <PowerVolume width={getScreenWidth()} height={getScreenHeight()}/>
    </>
}

export function Zone2OfficeRoom() {
    return <OfficeRoom room={"Zone2"} mediaComponent={Zone2MediaCenterScreen}/>
}