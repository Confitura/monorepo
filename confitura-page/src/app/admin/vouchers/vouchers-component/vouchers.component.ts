import {Component, OnInit, ViewChild} from '@angular/core';
import {VouchersService} from '../vouchers.service';
import {Voucher} from '../voucher.model';
import {MatPaginator, MatSort, MatTableDataSource} from '@angular/material';
import {FileUploader} from 'ng2-file-upload';
import {environment} from '../../../../environments/environment';
import {CurrentUser} from '../../../core/security/current-user.service';
import {ParticipantService} from '../../participants/participant.service';
import {ConfirmationService} from '../../../core/confirmation.service';

@Component({
  selector: 'cf-vouchers',
  templateUrl: './vouchers.component.html',
  styleUrls: ['./vouchers.component.css']
})
export class VouchersComponent implements OnInit {


  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  displayedColumns = [
    'originalBuyer',
    'emailSent',
    'createdBy',
    'createdDate',
    'lastModifiedBy',
    'lastModifiedDate',
  ];

  private vouchers: Voucher[];
  dataSource: MatTableDataSource<Voucher> = new MatTableDataSource<Voucher>();


  uploader: FileUploader;
  uploadResponse;

  constructor(private user: CurrentUser,
              private service: ParticipantService,
              private vouchersService: VouchersService,
              private confirmation: ConfirmationService) {

    this.uploader = new FileUploader({
      authToken: this.user.getToken(),
      url: `${environment.API_URL}participants/upload`,
      autoUpload: true,
    });

    this.uploader.onBeforeUploadItem = () => this.uploadResponse = null;
    this.uploader.onSuccessItem = (item, response) => this.showResponse(response);
    this.uploader.onCompleteAll = () => this.refresh();
    this.refresh();
  }

  private refresh() {
    this.vouchersService.getVouchers().subscribe(it => {
      this.vouchers = it;
      this.dataSource.data = it;
    });
  }

  ngOnInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  applyFilter(filterValue: string) {
    filterValue = filterValue.trim(); // Remove whitespace
    filterValue = filterValue.toLowerCase(); // Datasource defaults to lowercase matches
    this.dataSource.filter = filterValue;
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  private showResponse(responseString: string) {
    this.uploadResponse = JSON.parse(responseString);
  }

  upload(input) {
    $(input).click();
  }


  sendReminder() {
    this.confirmation.show('you want to send reminder emails to all buyers of unused vouchers?')
      .then(() => this.service.sendReminder().subscribe());
  }


}
