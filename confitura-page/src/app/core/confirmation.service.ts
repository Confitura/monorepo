import {Injectable} from '@angular/core';
import 'sweetalert';
import {CONFIRM_KEY, defaultButtonList, getButtonListOpts} from 'sweetalert/typings/modules/options/buttons';

@Injectable()
export class ConfirmationService {
  show(message: string): Promise<any> {
    return swal({
        title: 'Are you sure...',
        text: message,
        buttons: getButtonListOpts(true)
      }
    );
    // return new Promise((resolve) => {
    //
    //   ;
    // });
  }
}
