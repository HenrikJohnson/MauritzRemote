import React, {useEffect, useState} from "react";
import {MediaItem, query} from "../utils/Api";
import {FlatList, Keyboard, ListRenderItemInfo, View} from "react-native";
import {ContentItem} from "./ContentItem";
import {TextInput, useTheme} from "react-native-paper";
import {useAppContent} from "./AppContex";
import {ContentListSort} from "./ContentListSort";

export function ContentList(props: { queue: string }) {
    const [data, setData] = useState([] as MediaItem[]);
    const [sort, setSort] = useState("Entered" as string);
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
        return <ContentItem theme={theme} queue={props.queue} item={itemProps.item} appContext={appContext}/>
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
        <View style={{flexDirection: "column-reverse", flex: 1}}>
            <FlatList
                data={data}
                renderItem={renderItem}
                onScrollBeginDrag={() => {
                    Keyboard.dismiss();
                }}
                refreshing={refreshing}
                initialNumToRender={50}
                onRefresh={() => {
                    setRefreshing(true);
                    fetchContents(true);
                }}
                onEndReached={() => {
                    fetchContents(false);
                }}
                onEndReachedThreshold={4}
                keyExtractor={(item: MediaItem) => String(item.itemId)}
            />
            <View style={{flexDirection: "row", columnGap: 2}}>
                <TextInput style={{flex: 1}} placeholder={"Search"} onChangeText={setSearch} value={search}/>
                <ContentListSort sort={sort} onSort={setSortFiltered}/>
            </View>
        </View>
    )
}
