export class MenuItem {
    label: string;
    url?: string;
    action?: Function;
    clazz?: string;
    show?: Function = () => true;
}