import {RemoteIconButton} from "./RemoteIconButton";

export function BasicPlayback(props: { width: number, height: number, postfix: string }) {
    const buttonSize = Math.min(Math.min((props.width - 20) / 6, props.height / 8), 140);

    return <>
        <RemoteIconButton style={{
            position: 'absolute',
            left: 0,
            bottom: 10,
        }}
                          size={buttonSize - 10}
                          action={"Rewind_In_" + props.postfix}
                          mode={"contained-tonal"}
                          icon={"rewind"}/>

        <RemoteIconButton style={{
            position: 'absolute',
            left: buttonSize + 10,
            bottom: 10,
        }}
                          size={buttonSize - 10}
                          icon={"play"}
                          action={"Play_In_" + props.postfix}
                          mode={"contained-tonal"}/>

        <RemoteIconButton style={{
            position: 'absolute',
            left: (buttonSize + 10) * 2,
            bottom: 10,
        }}
                          size={buttonSize - 10}
                          icon={"fast-forward"}
                          action={"Fast_Forward_In_" + props.postfix}
                          mode={"contained-tonal"}/>
    </>;
}

