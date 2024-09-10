import React, {useCallback, useRef, useState} from "react";
import {View, FlatList, Keyboard, Pressable} from "react-native";
import {TextInput, Text, useTheme} from "react-native-paper";

const CRITERIA_OPTIONS = [
    "Entered",
    "Toplist",
    "Artist",
    "Title",
    "Last Played",
    "Album"
];

export function ContentListSort(props: {sort: string, onSort: (sort: string) => void}) {
    const [userinput, setUserinput] = useState(props.sort);
    const [show, setShow] = useState(false);
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
        (item : string) => {
            setShow(false)
            props.onSort(item)
        },
        [show, userinput]
    );

    return <>
        <TextInput
            placeholder={show ?'' :'Mr'}
            value={props.sort}
            disabled={show}
            selectTextOnFocus={false}
            style={{marginRight: -20}}
            onFocus={openPicker}
            right={<TextInput.Icon style={{marginLeft: -20}} onPress={togglePicker} icon={show ? "chevron-up" : "chevron-down"} />}
        />
        {show ?
            <FlatList
                style={{ backgroundColor: theme.colors.onSecondary, elevation:1, zIndex: 22, width: '100%', marginTop: 60, position: 'absolute' }}
                data={CRITERIA_OPTIONS}
                renderItem={({ item, index }) => (
                    <Pressable
                        onPress={(e) => {
                            hidePicker(item);
                            e.stopPropagation();
                        }}>
                        <Text style={{padding:10}} variant={"titleMedium"}>
                            {item}
                        </Text>
                    </Pressable>
                )}
                keyExtractor={item => item}
            />
            : null}
    </>
}
