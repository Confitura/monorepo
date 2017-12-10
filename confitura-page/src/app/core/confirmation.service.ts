import {Injectable} from '@angular/core';
import 'sweetalert';

@Injectable()
export class ConfirmationService {
  show(message: string): Promise<any> {
    return new Promise((resolve) => {
      swal({
          title: 'Are you sure...',
          text: message
        },
        () => resolve());
    });
  }
}
