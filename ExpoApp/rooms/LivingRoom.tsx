import {createMaterialTopTabNavigator} from "@react-navigation/material-top-tabs";
import {RequireAuth} from "../components/RequireAuth";
import MediaCenterIcon from "../assets/icons/mediacenter.svg";
import CableIcon from "../assets/icons/cable.svg";
import SwedishIcon from "../assets/icons/swedish.svg";
import {RoomNavigator} from "../components/RoomNavigator";
import {CableScreen} from "../screens/CableScreen";
import {LivingRoomMediaCenterScreen} from "../screens/MediaCenterScreen";
import {CompressSwedishScreen} from "../screens/SwedishScreen";
import {apiSend} from "../utils/Api";
import {PowerVolume} from "../components/PowerVolume";
import React from "react";
import {getScreenHeight, getScreenWidth} from "../utils/Layout";
import {useAppContent} from "../components/AppContex";

const Tab = createMaterialTopTabNavigator();

export function LivingRoom() {
    const keyboard = useAppContent();

    return <RequireAuth>
        <RoomNavigator screens={["Media", "Roku", "Swedish"]} room={"Livingroom"}
                       onTabPress={(index) => {
                           let action = "Media_Center";
                           switch (index) {
                               case 1:
                                   action = "Cable";
                                   break;
                               case 2:
                                   action = "AmazonTV";
                                   break;
                           }
                           apiSend(keyboard, `Turn_${action}_On`);
                       }}>
            <Tab.Screen name="Media" component={LivingRoomMediaCenterScreen} options={{
                tabBarIcon: (props) => <MediaCenterIcon width={32} height={32} fill={props.color}/>
            }}/>
            <Tab.Screen name="Roku" component={CableScreen} options={{
                tabBarIcon: (props) => <CableIcon width={32} height={32} fill={props.color}/>
            }}/>
            <Tab.Screen name="Swedish" component={CompressSwedishScreen} options={{
                tabBarIcon: (props) => <SwedishIcon width={32} height={32} fill={props.color}/>
            }}/>
        </RoomNavigator>
        <PowerVolume width={getScreenWidth()} height={getScreenHeight()} includeCompress={true}/>
    </RequireAuth>
}
