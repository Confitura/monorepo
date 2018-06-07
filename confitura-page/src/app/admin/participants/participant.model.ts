import {Voucher} from '../vouchers/voucher.model';

export class Participant {
  id: string;
  voucher: Voucher = new Voucher();
  name: string;
  email: string;
  originalBuyer: string;
  city: string;
  size: string;
  experience: string;
  role: string;
  gender: string;
  creationDate: string;
  registrationDate: string;
  ticketSendDate: string;
  mealOption: string;

  public constructor(obj: any = {}) {
    Object.assign(this, obj);
  }
}

