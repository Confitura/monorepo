import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {PartnerEditComponent} from './partners/partner/partner-edit.component';
import {IsAdminGuard} from './is-admin-guard.service';
import {LoginComponent} from '../pages/login/login.component';
import {UserListComponent} from './users/user-list/user-list.component';
import {ParticipantListComponent} from './participants/participant-list.component';
import {AgendaComponent} from './agenda/agenda.component';
import {ScannerComponent} from './scanner/scanner.component';
import {IsPrivilegedGuard} from './is-privileged-guard.service';
import {ProfileViewComponent} from '../profile/profile-view/profile-view.component';
import {VoteListComponent} from './votes/vote-list/vote-list.component';
import {VouchersComponent} from './vouchers/vouchers-component/vouchers.component';
import {DashboardComponent} from './dashboard/dashboard.component';

const routes: Routes = [

  {
    path: '',
    component: LoginComponent,
  },
  {
    path: '',
    canActivateChild: [IsPrivilegedGuard],
    children: [
      {path: 'dashboard', component: DashboardComponent},
      {path: 'scanner', component: ScannerComponent},
      {path: 'scanner/:id', component: ScannerComponent},
      {path: 'users', component: UserListComponent, canActivate: [IsAdminGuard]},
      {path: 'users/:id', component: ProfileViewComponent, canActivate: [IsAdminGuard]},
      {path: 'partners/add', component: PartnerEditComponent, canActivate: [IsAdminGuard]},
      {path: 'partners/edit/:id', component: PartnerEditComponent, canActivate: [IsAdminGuard]},
      {path: 'agenda', component: AgendaComponent, canActivate: [IsAdminGuard]},
      {path: 'votes', component: VoteListComponent, canActivate: [IsAdminGuard]},
      {path: 'participants', component: ParticipantListComponent, canActivate: [IsAdminGuard]},
      {path: 'vouchers', component: VouchersComponent, canActivate: [IsAdminGuard]}]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  providers: [IsAdminGuard, IsPrivilegedGuard]
})
export class AdminRoutingModule {

}
