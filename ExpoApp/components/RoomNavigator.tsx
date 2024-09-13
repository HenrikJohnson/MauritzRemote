import {ActivityIndicator, useTheme} from "react-native-paper";
import React, {useEffect, useState} from "react";
import {currentRoomPage, disableButtons, enableButtons, setRoomPage} from "../utils/Api";
import {View} from "react-native";
import {createMaterialTopTabNavigator} from "@react-navigation/material-top-tabs";
import {useAppContent} from "./AppContex";
import {getRoom} from "../utils/Storage";

const Tab = createMaterialTopTabNavigator();

export function RoomNavigator(props: {
    children?: React.ReactNode,
    screens: string[],
    tabNavigator?: React.ComponentType,
    onTabPress?: (index: number) => void,
    room: string
}) {
    const theme = useTheme();
    const appContext = useAppContent();

    const [page, setPage] = useState(props.screens.length > 1 ? -1 : 0);

    async function fetchCurrentPage() {
        setPage(await currentRoomPage(appContext, props.room));
    }

    async function roomPageChanged(index: number) {
        await setRoomPage(appContext, index, props.room);
    }

    useEffect(() => {
        if (getRoom() === props.room && props.screens.length > 1 && page < 0) {
            fetchCurrentPage();
        }
    }, [appContext.refreshToken]);

    useEffect(() => {
        if (getRoom() !== props.room && page >= 0 && props.screens.length > 1) {
            setPage(-1);
        }
    });

    if (page >= 0) {
        let tabName = props.screens[page];

        return <Tab.Navigator keyboardDismissMode={"on-drag"} initialRouteName={tabName} screenListeners={({navigation}) => ({
            swipeStart: (e) => {
                disableButtons();
            },
            swipeEnd: (e) => {
                enableButtons();
            },
            state: () => {
                const index = navigation.getState()?.index ?? 0;
                setPage(index);
            },
            tabPress: (e) => {
                const index = navigation.getState()?.routes.findIndex((route: any) => route.key === e.target);
                if (index == page) {
                    roomPageChanged(index);
                    if (props.onTabPress) {
                        props.onTabPress(index);
                    }
                }
                setPage(index);
            }
        })}>
            {props.children}
        </Tab.Navigator>
    } else {
        return <View style={{
            backgroundColor: theme.colors.background,
            flex: 1,
            alignItems: 'center',
            justifyContent: 'center',
        }}>
            <ActivityIndicator size={"large"}/>
        </View>
    }
}
