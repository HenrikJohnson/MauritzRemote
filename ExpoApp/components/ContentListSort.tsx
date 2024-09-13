import React, {useCallback, useRef, useState} from "react";
import {Keyboard, View} from "react-native";
import {Button, TextInput, useTheme} from "react-native-paper";
import {ContainerWithDimensions} from "./ContainerWithDimensions";

const CRITERIA_OPTIONS = [
    "Entered",
    "Toplist",
    "Artist",
    "Title",
    "Last Played",
    "Album"
];

export function ContentListSort(props: { sort: string, onSort: (sort: string) => void }) {
    const [userinput, setUserinput] = useState(props.sort);
    const [show, setShow] = useState(false);
    const ref = useRef<View>();
    const theme = useTheme();

    const openPicker = useCallback(
        () => {
            Keyboard.dismiss()
            setShow(true)
        },
        [show]
    );

    const togglePicker = useCallback(
        () => {
            Keyboard.dismiss()
            setShow(!show)
        },
        [show]
    );

    const hidePicker = useCallback(
        (item: string) => {
            setShow(false)
            props.onSort(item)
        },
        [show, userinput]
    );

    return <ContainerWithDimensions>
        {({width, height}) => {
            return <>
                <TextInput
                    placeholder={show ? '' : 'Mr'}
                    value={props.sort}
                    disabled={show}
                    selectTextOnFocus={false}
                    style={{marginRight: -20}}
                    onFocus={openPicker}
                    // @ts-ignore
                    ref={ref}
                    right={<TextInput.Icon style={{marginLeft: -20}} onPress={togglePicker}
                                           icon={show ? "chevron-up" : "chevron-down"}/>}
                />
                {show &&
                    <View
                        style={{
                            elevation: 1,
                            backgroundColor: theme.colors.onSecondary,
                            width: width,
                            top: height,
                            position: 'absolute'
                        }}
                    >
                        {
                            CRITERIA_OPTIONS.map(item => <Button key={item} mode={"text"}
                                                                 contentStyle={{"alignSelf": "flex-start"}}
                                                                 onPress={(e) => {
                                                                     hidePicker(item);
                                                                     e.stopPropagation();
                                                                     ref?.current?.blur();
                                                                 }}>
                                    {item}
                                </Button>
                            )
                        }
                    </View>
                }
            </>
        }}
    </ContainerWithDimensions>
}
