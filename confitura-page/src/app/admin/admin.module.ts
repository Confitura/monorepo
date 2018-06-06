import {NgModule} from '@angular/core';
import {AdminRoutingModule} from './admin-routing.module';
import {PartnerEditComponent} from './partners/partner/partner-edit.component';
import {SharedModule} from '../shared/shared.module';
import {UserListComponent} from './users/user-list/user-list.component';
import {ParticipantListComponent} from './participants/participant-list.component';
import {FileUploadModule} from 'ng2-file-upload';
import {AgendaComponent} from './agenda/agenda.component';
import {TimeSlotComponent} from './agenda/time-slot.component';
import {RoomComponent} from './agenda/room.component';
import {AgendaEntryComponent} from './agenda/agenda-entry.component';
import {PresentationPickerComponent} from './agenda/presentaion-picker/presentaion-picker.component';
import {ScannerComponent} from './scanner/scanner.component';
import {
  MatButtonModule,
  MatChipsModule,
  MatDividerModule,
  MatFormFieldModule,
  MatIconModule,
  MatInputModule,
  MatMenuModule,
  MatPaginatorModule,
  MatRadioModule,
  MatSortModule,
  MatTableModule,
  MatDialogModule,
  MatSelectModule,
  MatProgressSpinnerModule
} from '@angular/material';
import {VoteListComponent} from './votes/vote-list/vote-list.component';
import { VouchersComponent } from './vouchers/vouchers-component/vouchers.component';
import { MailService } from './users/mailing/mail.service';
import { MailModalComponent } from './users/mailing/mail-modal/mail-modal.component';

@NgModule({
  imports: [
    AdminRoutingModule,
    SharedModule,
    FileUploadModule,
    MatTableModule,
    MatChipsModule,
    MatRadioModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatButtonModule,
    MatMenuModule,
    MatIconModule,
    MatDividerModule,
    MatPaginatorModule,
    MatSortModule,
    MatDialogModule,
    MatSelectModule,
    MatProgressSpinnerModule
  ],
  declarations: [
    PartnerEditComponent,
    UserListComponent,
    ParticipantListComponent,
    AgendaComponent,
    TimeSlotComponent,
    RoomComponent,
    AgendaEntryComponent,
    PresentationPickerComponent,
    ScannerComponent,
    VoteListComponent,
    VouchersComponent
    MailModalComponent
  ],
  providers: [
    MailService
  ],
  entryComponents: [
    MailModalComponent
  ]
})
export class AdminModule {

}
