import React, {useEffect, useState} from "react";
import {GestureHandlerRootView, RectButton, Swipeable} from "react-native-gesture-handler";
import {makeApiCall, queueContents, QueueItem} from "../utils/Api";
import {Animated} from "react-native";
import {ContentItem, ContentItemProps} from "./ContentItem";
import ReorderableList, {ReorderableListRenderItemInfo, ReorderableListReorderEvent} from "react-native-reorderable-list";
import {IconButton, Text, useTheme} from "react-native-paper";
import {getRoom} from "../utils/Storage";
import {useAppContent} from "./AppContex";

export interface DeletableContentItemProps extends ContentItemProps {
    item: QueueItem,
    onDelete: () => void
}

function DeletableContentItem(props: DeletableContentItemProps) {
    const theme = useTheme();

    function renderRightActions(dragX: any) {

        async function deleteQueueItem() {
            try {
                props.onDelete();
                await makeApiCall(props.appContext, "/queue/" + getRoom() + "/" + props.queue + "/" + props.item.queueId, {
                    method: "DELETE"
                });
            } catch (e) {
            }
        }

        return (
            <Animated.View
                style={{
                    justifyContent: 'center',
                    alignItems: 'center', opacity: 0.7
                }}>
                <RectButton
                    style={{
                        width: "100%",
                        height: "100%",
                        justifyContent: 'center',
                        alignItems: 'center'
                    }}
                    onPress={() => deleteQueueItem()}>
                    <IconButton size={40} icon={"delete"}
                                containerColor={theme.colors.errorContainer} iconColor={theme.colors.error}
                                onPress={() => deleteQueueItem()}
                    />
                </RectButton>
            </Animated.View>
        );
    }

    return <Swipeable renderRightActions={(progress, dragX) => renderRightActions(dragX)}>
        <ContentItem item={props.item} queue={props.queue} appContext={props.appContext}
                     onLongPress={props.onLongPress} isDragged={props.isDragged}/>
    </Swipeable>
}


export function QueueList(props: {queue: string}) {
    const [data, setData] = useState([] as QueueItem[]);
    const appContext = useAppContent();

    async function fetchContents() {
        const newData = await queueContents(appContext, props.queue);
        setData(newData);
    }

    useEffect(() => {
        fetchContents();
    }, [props.queue, appContext.queueState]);


    function renderItem(itemProps: ReorderableListRenderItemInfo<QueueItem>) {
        if (itemProps.index === 0)
            return <ContentItem item={itemProps.item} queue={props.queue} appContext={appContext}/>
        else
            return <DeletableContentItem item={itemProps.item} queue={props.queue} appContext={appContext}
                                         onDelete={() => {
                                             const newData = [...data];
                                             newData.splice(itemProps.index, 1);
                                             setData([]);
                                             setData(newData);
                                         }}/>
    }

    function handleReorder({fromIndex, toIndex}: ReorderableListReorderEvent) {
        if (toIndex > 0 && toIndex != fromIndex) {
            const newData = [...data];
            const item = newData[fromIndex].queueId;
            const afterItem = newData[toIndex < fromIndex ? toIndex - 1 :  toIndex].queueId;

            try {
                makeApiCall(appContext, "/queue/" + getRoom() + "/" + props.queue + "/" + item + "/" + afterItem, {
                    method: "PUT"
                });

                newData.splice(toIndex, 0, newData.splice(fromIndex, 1)[0]);
                setData(newData);
            } catch (e) {
            }
        } else {
            fetchContents();
        }
    }

    return (
        <GestureHandlerRootView>
            <ReorderableList
                data={data}
                onReorder={handleReorder}
                renderItem={renderItem}
                keyExtractor={(item: QueueItem) => String(item.queueId)}
                dragScale={1.025}
            />
        </GestureHandlerRootView>
    );
};
