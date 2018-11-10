import {Component, OnInit, ViewChild} from '@angular/core';
import {ParticipantService} from './participant.service';
import {Participant} from './participant.model';
import {CurrentUser} from '../../core/security/current-user.service';
import {ConfirmationService} from '../../shared/confirmation/confirmation.service';
import {MatPaginator, MatSort, MatTableDataSource} from '@angular/material';

@Component({
  templateUrl: './participant-list.component.html'
})
export class ParticipantListComponent implements OnInit {


  list: Participant[];
  uploadResponse;


  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  dataSource: MatTableDataSource<Participant>;

  displayedColumns = [
    'name',
    'email',
    'gender',
    'size',
    'arrivalDate',
    'registeredBy',
    'ticketSendDate',
    'surveySendDate',
    'voucher'
  ];

  constructor(private service: ParticipantService,
              private user: CurrentUser,
              private confirmation: ConfirmationService) {
  }

  ngOnInit(): void {
    this.dataSource = new MatTableDataSource();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    this.service.getAll()
      .subscribe(list => {
        this.list = list;
        this.dataSource.data = list;
      });

  }

  applyFilter(filterValue: string) {
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  sendTickets() {
    this.confirmation.show('you want to send tickets to participants which haven\'t received  one yet?')
      .then(() => this.service.sendTickets().subscribe());
  }

  sendSurveys() {
    this.confirmation.show('you want to send surveys to all attendees?')
      .then(() => this.service.sendSurveys().subscribe());
  }

  private showResponse(responseString: string) {
    this.uploadResponse = JSON.parse(responseString);
  }
}
