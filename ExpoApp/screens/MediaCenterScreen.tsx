import {View} from "react-native";
import {NavigationButtons} from "../components/NavigationButtons";
import {ContainerWithDimensions} from "../components/ContainerWithDimensions";

import React from "react";
import {BasicPlayback} from "../components/BasicPlayback";
import {AdditionalPlayback} from "../components/AdditionalPlayback";
import {MediaType} from "../components/MediaType";
import {MediaCenterSettings} from "../components/MediaCenterSettings";

export function MediaCenterScreen(props: { includeCompress?: boolean, room: string }) {
    return <ContainerWithDimensions style={{
        flexDirection: 'column-reverse',
        justifyContent: 'space-between',
        rowGap: 5,
        flex: 1,
        padding: 10,
        height: "100%",
    }}>
        {({width, height}) => {
            return <>
                <View style={{flex: 1}}/>

                <AdditionalPlayback width={width} height={height} postfix={"Media_Center"}/>
                <BasicPlayback width={width} height={height} postfix={"Media_Center"}/>

                <View style={{
                    flex: 2,
                    width: "100%",
                    justifyContent: "center",
                }}>
                    <MediaType style={{
                        width: "70%",
                        height: "100%",
                        flexDirection: 'column',
                        columnGap: 5,
                    }}
                               room={props.room}
                               width={width}
                               height={height}/>
                </View>

                <View style={{
                    flex: 2,
                    flexDirection: 'row',
                    columnGap: 5,
                    width: "100%"
                }}>
                    <MediaCenterSettings style={{
                        flex: 1,
                        height: "100%",
                        width: "100%",
                        justifyContent: "center"
                    }}/>
                    <NavigationButtons style={{
                        height: "100%",
                        flex: 1,
                        width: "100%",
                        justifyContent: "center"
                    }} postFix={"Media_Center"}
                                       additionalText={["Home", "Context", "Info", "Back"]}
                                       additionalActions={[
                                           "Show_Menu_In_Media_Center",
                                           "Show_Context_In_Media_Center",
                                           "Show_Info_In_Media_Center",
                                           "Go_Back_In_Media_Center"]}/>
                </View>
            </>;
        }}
    </ContainerWithDimensions>
}

export function LivingRoomMediaCenterScreen() {
    return MediaCenterScreen({includeCompress: true, room: "Livingroom"});
}

export function OfficeMediaCenterScreen() {
    return MediaCenterScreen({room: "Office"});
}

export function Zone2MediaCenterScreen() {
    return MediaCenterScreen({room: "Zone2"});
}
