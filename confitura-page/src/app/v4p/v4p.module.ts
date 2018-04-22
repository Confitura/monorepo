import {NgModule} from '@angular/core';
import {V4pRoutingModule} from './v4p-routing.module';
import {SharedModule} from '../shared/shared.module';
import {VotingComponent} from './voting/voting.component';
import {V4pService} from './shared/v4p.service';
import {V4pStartComponent} from './start/v4p-start.component';
import {V4pEndComponent} from './end/v4p-end.component';
import {MatButtonModule, MatIconModule} from '@angular/material';

import {HotkeyModule} from 'angular2-hotkeys';

@NgModule({
  imports: [
    SharedModule,
    V4pRoutingModule,
    MatIconModule,
    MatButtonModule,
    HotkeyModule,
  ],
  declarations: [
    VotingComponent,
    V4pStartComponent,
    V4pEndComponent
  ],
  providers: [V4pService]
})
export class V4pModule {

}
