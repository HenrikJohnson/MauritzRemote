import {View} from "react-native";
import {NavigationButtons} from "../components/NavigationButtons";
import {BottomSwedishChannelButtons, TopSwedishChannelButtons} from "../components/ChannelButtons";
import {ContainerWithDimensions} from "../components/ContainerWithDimensions";

import React from "react";
import {BasicPlayback} from "../components/BasicPlayback";

export function SwedishScreen(props: { includeCompress?: boolean }) {
    return <ContainerWithDimensions style={{
        flexDirection: 'column-reverse',
        justifyContent: 'space-between',
        rowGap: 5,
        padding: 10,
        height: "100%",
    }}>
        {({width, height}) => {
            return <>
                <View style={{flex: 1}}/>
                <BasicPlayback width={width} height={height} postfix={"AmazonTV"}/>
                <View style={{
                    flex: 2,
                    flexDirection: 'row',
                    columnGap: 5,
                    width: "100%",
                }}>
                    <BottomSwedishChannelButtons style={{flex: 1, width: "100%", height: "100%"}}/>
                    <View style={{flex: 1}}/>
                </View>
                <View style={{
                    flex: 2,
                    flexDirection: 'row',
                    columnGap: 5,
                    width: "100%"
                }}>
                    <TopSwedishChannelButtons style={{flex: 1, width: "100%", height: "100%"}}/>
                    <NavigationButtons style={{
                        height: "100%",
                        flex: 1,
                        width: "100%",
                        justifyContent: "center"
                    }} postFix={"AmazonTV"}
                                       additionalText={["Home", "Search", "Menu", "Back"]}
                                       additionalActions={["Go_To_Home_In_AmazonTV", "Search_In_AmazonTV", "Menu_In_AmazonTV", "Go_Back_In_AmazonTV"]}/>
                </View>
            </>;
        }}
    </ContainerWithDimensions>
}

export function CompressSwedishScreen() {
    return SwedishScreen({includeCompress: true});
}
