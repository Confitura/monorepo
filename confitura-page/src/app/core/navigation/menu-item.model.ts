export class MenuItem {
  label: string;
  url?: string;
  action?: Function;
  clazz?;
  children?: MenuItem[];
  show?: Function = () => true;
}
