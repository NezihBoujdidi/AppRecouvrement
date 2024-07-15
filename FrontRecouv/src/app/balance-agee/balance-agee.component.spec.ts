import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BalanceAgeeComponent } from './balance-agee.component';

describe('BalanceAgeeComponent', () => {
  let component: BalanceAgeeComponent;
  let fixture: ComponentFixture<BalanceAgeeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BalanceAgeeComponent]
    });
    fixture = TestBed.createComponent(BalanceAgeeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
