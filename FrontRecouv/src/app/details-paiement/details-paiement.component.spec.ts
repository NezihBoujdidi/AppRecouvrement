import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailsPaiementComponent } from './details-paiement.component';

describe('DetailsPaiementComponent', () => {
  let component: DetailsPaiementComponent;
  let fixture: ComponentFixture<DetailsPaiementComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DetailsPaiementComponent]
    });
    fixture = TestBed.createComponent(DetailsPaiementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
