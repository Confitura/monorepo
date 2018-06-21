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
  MatCardModule,
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
import {DashboardComponent} from './dashboard/dashboard.component';
import { Ng2GoogleChartsModule } from 'ng2-google-charts';


@NgModule({
  imports: [
    Ng2GoogleChartsModule,
    AdminRoutingModule,
    SharedModule,
    FileUploadModule,
    MatCardModule,
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
    MatCardModule,
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
    VouchersComponent,
    DashboardComponent,
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
