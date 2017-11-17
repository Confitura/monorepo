export class MenuItem {
  label: string;
  url?: string;
  action?: Function;
  clazz?: string;
  children?: MenuItem[];
  show?: Function = () => true;
}
