import {View} from "react-native";
import {StyleProp} from "react-native/Libraries/StyleSheet/StyleSheet";
import {ViewStyle} from "react-native/Libraries/StyleSheet/StyleSheetTypes";
import {ContainerWithDimensions} from "./ContainerWithDimensions";
import React, {FC} from "react";
import {FlexContainer} from "./FlexContainer";
import {SvgProps} from "react-native-svg";

import HuluIcon from "../assets/icons/hulu.svg";
import PlexIcon from "../assets/icons/plex.svg";
import AmazonIcon from "../assets/icons/amazon_video.svg";
import ParamountIcon from "../assets/icons/paramount.svg";
import AppleIcon from "../assets/icons/apple.svg";
import HboIcon from "../assets/icons/hbo.svg";
import DisneyIcon from "../assets/icons/disney.svg";
import NetflixIcon from "../assets/icons/netflix.svg";
import SvtPlayIcon from "../assets/icons/svt_play.svg";
import XBoxIcon from "../assets/icons/xbox.svg";
import SwitchIcon from "../assets/icons/switch.svg";
import GamingPCIcon from "../assets/icons/gaming_pc.svg";
import WiiIcon from "../assets/icons/wii.svg";
import VCIcon from "../assets/icons/webcam.svg";
import VCSecondIcon from "../assets/icons/webcam2.svg";
import {RemoteIconButton} from "./RemoteIconButton";
import {useTheme} from "react-native-paper";

export function ColumnOfButtons(props: {children: React.ReactNode, size: number}) {
    return <View style={{
        flexDirection: 'column',
        justifyContent: 'space-between',
        alignSelf: "flex-end",
        rowGap: 5,
        width: props.size,
        height: "100%"
    }}>
        {props.children}
    </View>
}

export function RowOfButtons(props: {children: React.ReactNode}) {
    return <View style={{
        flexDirection: 'row',
        justifyContent: 'space-between',
        columnGap: 5,
        flex: 1
    }}>
        {props.children}
    </View>
}

function ChannelIcon(props: { size: number, action: string, alignItems: "flex-start" | "flex-end", icon: FC<SvgProps> }) {
    const buttonSize = props.size / 3;
    const iconSize = props.size / 3.5;

    const theme = useTheme();

    return <FlexContainer alignItems={props.alignItems}>
        <RemoteIconButton mode={"outlined"} containerColor={theme.colors.outline} size={buttonSize} action={props.action} icon={() => <props.icon width={iconSize} height={iconSize}/> }/>
    </FlexContainer>;
}

export function TopChannelButtons(props: {
    style?: StyleProp<ViewStyle>,
}) {
    return <ContainerWithDimensions style={props.style}>
        {({width, height}) => {
            const size = Math.min(width, height);
            return <ColumnOfButtons size={size}>
                <RowOfButtons>
                    <ChannelIcon size={size} icon={AppleIcon} alignItems={"flex-start"} action={"Go_To_Apple_In_Cable"}/>
                    <ChannelIcon size={size} icon={PlexIcon} alignItems={"flex-end"} action={"Go_To_Plex_In_Cable"}/>
                </RowOfButtons>
                <RowOfButtons>
                    <ChannelIcon size={size} icon={DisneyIcon} alignItems={"flex-start"} action={"Go_To_Disney_In_Cable"}/>
                    <ChannelIcon size={size} icon={ParamountIcon} alignItems={"flex-end"} action={"Go_To_Paramount_In_Cable"}/>
                </RowOfButtons>
            </ColumnOfButtons>
        }}
    </ContainerWithDimensions>
}

export function BottomChannelButtons(props: {
    style?: StyleProp<ViewStyle>,
}) {
    return <ContainerWithDimensions style={props.style}>
        {({width, height}) => {
            const size = Math.min(width, height);
            return <ColumnOfButtons size={size}>
                <RowOfButtons>
                    <ChannelIcon size={size} icon={NetflixIcon} alignItems={"flex-start"} action={"Go_To_Netflix_In_Cable"}/>
                    <ChannelIcon size={size} icon={HuluIcon} alignItems={"flex-end"} action={"Go_To_Hulu_In_Cable"}/>
                </RowOfButtons>
                <RowOfButtons>
                    <ChannelIcon size={size} icon={AmazonIcon} alignItems={"flex-start"} action={"Go_To_Amazon_In_Cable"}/>
                    <ChannelIcon size={size} icon={HboIcon} alignItems={"flex-end"} action={"Go_To_HBO_In_Cable"}/>
                </RowOfButtons>
            </ColumnOfButtons>
        }}
    </ContainerWithDimensions>
}

export function BottomSwedishChannelButtons(props: {
    style?: StyleProp<ViewStyle>,
}) {
    return <ContainerWithDimensions style={props.style}>
        {({width, height}) => {
            const size = Math.min(width, height);
            return <ColumnOfButtons size={size}>
                <RowOfButtons>
                    <ChannelIcon size={size} icon={NetflixIcon} alignItems={"flex-start"} action={"Go_To_Netflix_In_AmazonTV"}/>
                    <FlexContainer/>
                </RowOfButtons>
                <RowOfButtons>
                    <ChannelIcon size={size} icon={AmazonIcon} alignItems={"flex-start"} action={"Go_To_Amazon_In_Cable"}/>
                    <FlexContainer/>
                </RowOfButtons>
            </ColumnOfButtons>
        }}
    </ContainerWithDimensions>
}

export function TopSwedishChannelButtons(props: {
    style?: StyleProp<ViewStyle>,
}) {
    return <ContainerWithDimensions style={props.style}>
        {({width, height}) => {
            const size = Math.min(width, height);
            return <ColumnOfButtons size={size}>
                <RowOfButtons>
                    <ChannelIcon size={size} icon={SvtPlayIcon} alignItems={"flex-start"} action={"Go_To_SVTPlay_In_Cable"}/>
                    <FlexContainer/>
                </RowOfButtons>
                <RowOfButtons>
                    <ChannelIcon size={size} icon={DisneyIcon} alignItems={"flex-start"} action={"Go_To_Disney_In_Cable"}/>
                    <FlexContainer/>
                </RowOfButtons>
            </ColumnOfButtons>
        }}
    </ContainerWithDimensions>
}

export function TopGameButtons(props: {
    style?: StyleProp<ViewStyle>,
}) {
    return <ContainerWithDimensions style={props.style}>
        {({width, height}) => {
            const size = Math.min(width, height);
            return <ColumnOfButtons size={size}>
                <RowOfButtons>
                    <ChannelIcon size={size} icon={SwitchIcon} alignItems={"flex-start"} action={"Go_To_Playstation_In_Game"}/>
                    <FlexContainer/>
                </RowOfButtons>
                <RowOfButtons>
                    <ChannelIcon size={size} icon={WiiIcon} alignItems={"flex-start"} action={"Go_To_Wii_In_Game"}/>
                    <FlexContainer/>
                </RowOfButtons>
            </ColumnOfButtons>
        }}
    </ContainerWithDimensions>
}

export function BottomGameButtons(props: {
    style?: StyleProp<ViewStyle>,
}) {
    return <ContainerWithDimensions style={props.style}>
        {({width, height}) => {
            const size = Math.min(width, height);
            return <ColumnOfButtons size={size}>
                <RowOfButtons>
                    <ChannelIcon size={size} icon={VCIcon} alignItems={"flex-start"} action={"Go_To_VC_In_Game"}/>
                    <ChannelIcon size={size} icon={VCSecondIcon} alignItems={"flex-end"} action={"Go_To_VC_Secondary_In_Game"}/>
                </RowOfButtons>
                <RowOfButtons>
                    <ChannelIcon size={size} icon={GamingPCIcon} alignItems={"flex-start"} action={"Go_To_PC_In_Game"}/>
                    <ChannelIcon size={size} icon={XBoxIcon} alignItems={"flex-end"} action={"Go_To_Xbox_In_Game"}/>
                </RowOfButtons>
            </ColumnOfButtons>
        }}
    </ContainerWithDimensions>
}
