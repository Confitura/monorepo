import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {VotingComponent} from './voting/voting.component';
import {V4pStartComponent} from './start/v4p-start.component';
import {V4pEndComponent} from './end/v4p-end.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'start'
  },
  {
    path: 'start',
    component: V4pStartComponent
  }, {
    path: 'voting',
    component: VotingComponent
  }, {
    path: 'end',
    component: V4pEndComponent
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)]
})
export class V4pRoutingModule {

}
