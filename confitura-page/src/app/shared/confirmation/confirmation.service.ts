import {Injectable} from '@angular/core';
import {MatDialog} from '@angular/material';
import {ConfirmationDialogComponent} from './confirmation-dialog.component';

@Injectable({
  providedIn: 'root'
})
export class ConfirmationService {
  constructor(public dialog: MatDialog) {
  }

  show(message: string): Promise<any> {
    return new Promise(resolve => {
      return this.dialog
        .open(ConfirmationDialogComponent, {
          width: '500px',
          data: {message: message}
        })
        .afterClosed()
        .toPromise()
        .then(confirmed => {
          if (confirmed) {
            resolve();
          }
        });
    });
  }

}
