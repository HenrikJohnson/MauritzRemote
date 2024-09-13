import {Button, Dialog, Icon, SegmentedButtons} from "react-native-paper";
import {useAppContent} from "./AppContex";
import React from "react";
import {useWindowDimensions, View} from "react-native";
import {NavigationContainer, useTheme} from "@react-navigation/native";
import {createBottomTabNavigator} from "@react-navigation/bottom-tabs";
import {QueueList} from "./QueueList";
import {ContentList} from "./ContentList";

const Tab = createBottomTabNavigator();

export function QueueManagement(props: { defaultQueue: string, initialRoute: "Queue" | "Contents" }) {
    const appContext = useAppContent();

    const theme = useTheme();
    const [queue, setQueue] = React.useState(props.defaultQueue);

    const dimensions = useWindowDimensions();

    return <Dialog visible={true} onDismiss={() => {
        appContext.setKeyboardView(undefined);
    }}>
        <Dialog.Title>
            <SegmentedButtons
                value={queue}
                buttons={[
                    {
                        icon: () => <Icon size={32} source={"music"}/>,
                        value: "Music",
                    },
                    {
                        icon: () => <Icon size={32} source={"movie-open"}/>,
                        value: "Movie",
                    },
                    {
                        icon: () => <Icon size={32} source={"television"}/>,
                        value: "Tv",
                    }
                ]} onValueChange={(e) => {
                setQueue(e);
            }
            }/>
        </Dialog.Title>
        <Dialog.Content>

            <View style={{
                height: dimensions.height - 180
            }}>
                <NavigationContainer theme={theme} independent={true}>
                    <Tab.Navigator initialRouteName={props.initialRoute}>
                        <Tab.Screen name="Queue" options={{
                            headerShown: false,
                            tabBarIcon: ({focused, size, color}) =>
                                <Icon size={size} source={"playlist-play"} color={color}/>
                        }}>
                            {(props) => <QueueList queue={queue}/>}
                        </Tab.Screen>
                        <Tab.Screen name="Contents" options={{
                            headerShown: false,
                            tabBarIcon: ({focused, size, color}) =>
                                <Icon size={size} source={"table-of-contents"} color={color}/>
                        }}>
                            {(props) => <ContentList queue={queue}/>}
                        </Tab.Screen>
                    </Tab.Navigator>
                </NavigationContainer>
            </View>
        </Dialog.Content>
        <Dialog.Actions>
            <Button onPress={() => {
                appContext.setKeyboardView(undefined);
            }}>Close</Button>
        </Dialog.Actions>
    </Dialog>
}