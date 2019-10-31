import {Component} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, Subscription} from 'rxjs';
import {timer} from 'rxjs/internal/observable/timer';


@Component({
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {

  tShirtsChartData = {
    chartType: 'ColumnChart',
    dataTable: [
      ['size', 'Male', 'Female'],
      ['S', 0, 0],
      ['M', 0, 0],
      ['L', 0, 0],
      ['XL', 0, 0],
      ['XXL', 0, 0]
    ],
    options: {'title': 'T-Shirts'},
  };

  foodChartData = {
    chartType: 'PieChart',
    dataTable: [
      ['option', 'count'],
      ['vegetarian', 0],
      ['meat', 0]
    ],
    options: {'title': 'Food option'},
  };

  vouchersChartData = {
    chartType: 'PieChart',
    dataTable: [
      ['option', 'count'],
      ['not user', 0],
      ['used', 0]
    ],
    options: {'title': 'Vouchers'},
  };

  usersChartData = {
    chartType: 'PieChart',
    dataTable: [
      ['option', 'count'],
      ['without voucher', 0],
      ['not registered as participant', 0],
      ['registered', 0]
    ],
    options: {'title': 'Users'},
  };

  presentationsChartData = {
    chartType: 'Table',
    dataTable: [
      ['option', 'count'],
      ['without voucher', 0],
      ['not registered as participant', 0],
      ['registered', 0]
    ],
    options: {'title': 'presentations'},
  };

  arrivalChartData = {
    chartType: 'ColumnChart',
    dataTable: [
      ['date', 'count'],
      [new Date(2018, 2, 1, 11), 0],
      [new Date(2018, 2, 2, 4), 5],
      [new Date(2018, 2, 3, 21), 22],
      [new Date(2018, 2, 4, 1), 27],
    ],
    options: {'title': 'arrivals'},
  };

  registrationsChartData = {
    chartType: 'ColumnChart',
    dataTable: [
      ['date', 'count'],
      [new Date(2018, 2, 1, 11), 0],
      [new Date(2018, 2, 2, 4), 5],
      [new Date(2018, 2, 3, 21), 22],
      [new Date(2018, 2, 4, 1), 27],
    ],
    options: {'title': 'registrations'},
  };

  arrivalTimestamp: Date;

  constructor(private client: HttpClient) {
    this.loadTshirts();
    this.loadMeals();
    this.loadVouchers();
    this.loadRegistrationStats();
    this.loadArrivals();
    this.loadRegistrations();
  }

  private loadRegistrations() {
    this.client.get<(string | number)[][]>('/dashboard/registrations').subscribe(it => {
      // @ts-ignore
      this.registrationsChartData.dataTable = it.map(row => [new Date(<string>row[0]), row[1]]);
    });
  }

  loadArrivals() {
    this.client.get<(string | number)[][]>('/dashboard/arrivals').subscribe(it => {
      // @ts-ignore
      this.arrivalChartData.dataTable = it.map(row => [new Date(<string>row[0]), row[1]]);
      this.arrivalTimestamp = new Date();
    });
  }

  private loadRegistrationStats() {
    this.client.get<(string | number)[][]>('/dashboard/registration').subscribe(it => {
      this.usersChartData.dataTable = it;
    });
  }

  private loadVouchers() {
    this.client.get<(string | number)[][]>('/dashboard/vouchers').subscribe(it => {
      this.vouchersChartData.dataTable = it;
    });
  }

  private loadMeals() {
    this.client.get<(string | number)[][]>('/dashboard/meal').subscribe(it => {
      this.foodChartData.dataTable = it;
    });
  }

  private loadTshirts() {
    this.client.get<(string | number)[][]>('/dashboard/tshirts').subscribe(it => {
      this.tShirtsChartData.dataTable = it;
    });
  }


}
