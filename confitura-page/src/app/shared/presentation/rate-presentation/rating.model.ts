export class Rate {
  id: string;
  value: string;
  comment: string;

  constructor(o: any = {}) {
    Object.assign(this, o);

  }
}
