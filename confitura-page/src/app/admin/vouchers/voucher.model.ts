export class Voucher {
  id: string;
  originalBuyer: string;
  emailSent: boolean;
  createdBy: string;
  createdDate: string;
  lastModifiedBy: string;
  lastModifiedDate: string;

  constructor(obj: any = {}) {
    Object.assign(this, obj);
  }
}
