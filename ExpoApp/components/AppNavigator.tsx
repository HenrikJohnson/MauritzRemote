import {getRoom, setRoom} from "../utils/Storage";
import {AppState, Platform, StyleSheet, View} from "react-native";
import {IconButton, Switch, Text} from "react-native-paper";
import {LivingRoom} from "../rooms/LivingRoom";
import LivingRoomIcon from "../assets/icons/livingroom.svg";
import {OfficeRoom, Zone2OfficeRoom} from "../rooms/OfficeRoom";
import OfficeIcon from "../assets/icons/office.svg";
import Zone2Icon from "../assets/icons/zone2.svg";
import {BedRoom} from "../rooms/BedRoom";
import BedRoomIcon from "../assets/icons/bedroom.svg";
import React, {useEffect, useState} from "react";
import {createDrawerNavigator, DrawerContentScrollView, DrawerItemList} from "@react-navigation/drawer";
import {useAppContent} from "./AppContex";
import {QueueManagement} from "./QueueManagement";

const Drawer = createDrawerNavigator();

function CustomDrawerContent(props: any) {
    const appContext = useAppContent();

    return (
        <DrawerContentScrollView{...props}
                                contentContainerStyle={{
                                    paddingTop: 0,
                                    justifyContent: 'space-between',
                                    height: '100%'
                                }}>
            <View>
                <View
                    style={styles.navigationHeader}
                >
                    <Text style={styles.navigationHeaderText} variant={"titleLarge"}>
                        Room
                    </Text>
                </View>
                <DrawerItemList {...props} />
            </View>
            <View style={{flexDirection: "row", alignItems: "center", padding: 8, paddingBottom: 32, columnGap: 8}}>
                <Text variant={"titleMedium"}>
                    Large navigation
                </Text>
                <Switch
                    value={appContext.expandedNavigation}
                    onValueChange={(value) => {
                        appContext.setExpandedNavigation(!appContext.expandedNavigation);
                    }}
                />
            </View>
        </DrawerContentScrollView>
    );
}

export function AppNavigator() {
    const appContext = useAppContent();

    useEffect(() => {
        const subscription = AppState.addEventListener('change',
            state => {
                if (state === "active")
                    appContext.updateRefreshToken();
            });
        return () => subscription.remove();
    }, []);

    return <>
        <Drawer.Navigator initialRouteName={getRoom()} drawerContent={CustomDrawerContent}
                          screenListeners={{
                              state: (e) => {
                                  const newCurrentRoom = e.data.state.routeNames[e.data.state.index];
                                  if (newCurrentRoom && getRoom() !== newCurrentRoom) {
                                      setRoom(newCurrentRoom);
                                      appContext.setKeyboardView(undefined);
                                      appContext.updateRefreshToken();
                                  }
                              }
                          }}
                          screenOptions={({navigation}) => ({
                              drawerStyle: {
                                  width: Platform.OS === "web" ? 300 : 200,
                              },
                              drawerLabelStyle: {
                                  marginLeft: -16,
                              },
                              headerLeft: props => <IconButton onPress={navigation.toggleDrawer}
                                                               icon={"menu"}/>,
                          })}
        >
            <Drawer.Screen name="Livingroom" component={LivingRoom} options={{
                drawerIcon: (props) => <LivingRoomIcon width={props.size} height={props.size}
                                                       fill={props.color}/>
            }}/>
            <Drawer.Screen name="Office" component={OfficeRoom} options={{
                drawerIcon: (props) => <OfficeIcon width={props.size} height={props.size} fill={props.color}/>
            }}/>
            <Drawer.Screen name="Zone2" component={Zone2OfficeRoom} options={{
                drawerIcon: (props) => <Zone2Icon width={props.size} height={props.size} fill={props.color}/>
            }}/>
            <Drawer.Screen name="Bedroom" component={BedRoom} options={{
                drawerIcon: (props) => <BedRoomIcon width={props.size} height={props.size} fill={props.color}/>
            }}/>
        </Drawer.Navigator>
        {(appContext.keyboardView === "Tv" || appContext.keyboardView === "Movie" || appContext.keyboardView === "Music") &&
            <QueueManagement defaultQueue={appContext.keyboardView} initialRoute={"Queue"}/>
        }
        {(appContext.keyboardView === "ContentsTv" || appContext.keyboardView === "ContentsMovie" || appContext.keyboardView === "ContentsMusic") &&
            <QueueManagement defaultQueue={appContext.keyboardView.substring(8)} initialRoute={"Contents"}/>
        }
    </>
}

const styles = StyleSheet.create({
    navigationHeader: {
        backgroundColor: '#5700f5',
        height: Platform.OS === "android" ? 80 : 40,
        alignItems: 'center',
        justifyContent: Platform.OS === "android" ? 'flex-end' : 'center',
        paddingBottom: Platform.OS === "android" ? 10 : 0,
    },
    navigationHeaderText: {color: 'white'}
});
