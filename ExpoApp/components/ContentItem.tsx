import {makeApiCall, MediaItem, QueueItem} from "../utils/Api";
import {Pressable, View} from "react-native";
import {Icon, MD3Theme, Text, useTheme} from "react-native-paper";
import React, {PureComponent} from "react";
import {getRoom} from "../utils/Storage";
import {AppContext} from "./AppContex";
import Color from "color";

function fixTitle(title?: string) {
    return (title ?? "").replace(/^Episode /, "");
}

export interface ContentItemProps {
    item: MediaItem,
    appContext: AppContext,
    theme: MD3Theme,
    disabled?: boolean,
    queue: string,
    onLongPress?: (() => void) | undefined,
    isDragged?: boolean | undefined
}

function calculateBackgroundColor(item: MediaItem, theme: MD3Theme) {
    if (!(item as QueueItem).queueId && (item.played??0) == 0) {
        const color = Color(theme.colors.background);
        if (color.luminosity() > 0.5)
            return "#f7f3f6";
        return "#242327";
    }
    return theme.colors.background;
}

export class ContentItem extends PureComponent<ContentItemProps> {

    async selectItem(item: MediaItem) {
        try {
            this.props.appContext.setNotification(`Selected ${item.artist} - ${item.title}`)
            await makeApiCall(this.props.appContext, `queue/${getRoom()}/${this.props.queue}/${item.itemId}`, {
                method: "POST"
            });
            this.props.appContext.setQueueState(this.props.appContext.queueState + 1);
        } catch (e) {
        }
    }

    render() {
        const backgroundColor = calculateBackgroundColor(this.props.item, this.props.theme);

        const contents = <View
            style={{flexDirection: "row", padding: 5, height: 85,
                backgroundColor: backgroundColor,
                opacity: this.props.isDragged ? 0.7 : 1.0}}>
            <View style={{flex: 1, marginRight: 5, justifyContent: "space-around"}}>
                <Text variant={"labelLarge"}>{this.props.item.artist}</Text>
                <Text variant={"labelMedium"}>{fixTitle(this.props.item.title)}</Text>
                <Text variant={"labelMedium"}>{this.props.item.album ?? ""}</Text>
            </View>
            {this.props.item.coverUrl &&
                <Icon size={80} source={{uri: this.props.item.coverUrl}}/>
            }
        </View>

        if (this.props.disabled || this.props.isDragged) {
            return contents;
        }

        return <Pressable onPress={() => this.selectItem(this.props.item)} onLongPress={this.props.onLongPress}>
            {contents}
        </Pressable>;
    }
}