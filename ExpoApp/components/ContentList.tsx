import React, {useEffect, useState} from "react";
import {MediaItem, query} from "../utils/Api";
import {FlatList, Keyboard, ListRenderItemInfo, Pressable, View} from "react-native";
import {ContentItem} from "./ContentItem";
import {PaperProvider, TextInput, useTheme} from "react-native-paper";
import {Dropdown, Option} from "react-native-paper-dropdown";
import {useAppContent} from "./AppContex";

const CRITERIA_OPTIONS : Option[] = [
    "Entered",
    "Toplist",
    "Artist",
    "Title",
    "Last Played",
    "Album"
].map((value) => ({label: value, value: value}));

export function ContentList(props: {queue: string}) {
    const [data, setData] = useState([] as MediaItem[]);
    const [sort, setSort] = useState("Entered" as string | undefined);
    const [refreshing, setRefreshing] = useState(false);
    const appContext = useAppContent();
    const theme = useTheme();

    const [search, setSearch] = useState("")

    async function fetchContents(clear: boolean) {
        if (clear) {
            const newData = await query(appContext, props.queue, sort ?? "", search, 0, 100);
            setData(newData);
        } else {
            const newData = await query(appContext, props.queue, sort ?? "", search, data.length, 100);
            setData([...data, ...newData]);
        }

        setRefreshing(false);
    }

    useEffect(() => {
        setRefreshing(true);
        fetchContents(true);
    }, [props.queue, search, sort]);

    function renderItem(itemProps: ListRenderItemInfo<MediaItem>) {
        return <ContentItem queue={props.queue} item={itemProps.item} appContext={appContext}/>
    }

    function setSortFiltered(sort?: string) {
        if (sort) {
            setSort(sort);
        } else {
            setRefreshing(true);
            fetchContents(true);
        }
    }

    return (
        <PaperProvider theme={theme}>
            <View>
                <View style={{flexDirection: "row", columnGap: 2}}>
                    <TextInput style={{flex: 1}} placeholder={"Search"} onChangeText={setSearch} value={search}/>
                    <Dropdown
                        label=" "
                        placeholder="Sort"
                        options={CRITERIA_OPTIONS}
                        value={sort}
                        onSelect={setSortFiltered}
                    />
                </View>
                <FlatList
                    data={data}
                    renderItem={renderItem}
                    onScrollBeginDrag={() => {
                        Keyboard.dismiss();
                    }}
                    refreshing={refreshing}
                    onRefresh={() => {
                        setRefreshing(true);
                        fetchContents(true);
                    }}
                    onEndReached={() => fetchContents(false)}
                    onEndReachedThreshold={0.5}
                    keyExtractor={(item: MediaItem) => String(item.itemId)}
                />
            </View>
        </PaperProvider>
    )
}
