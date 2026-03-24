import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcurementForm } from './procurement-form';

describe('ProcurementForm', () => {
  let component: ProcurementForm;
  let fixture: ComponentFixture<ProcurementForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProcurementForm],
    }).compileComponents();

    fixture = TestBed.createComponent(ProcurementForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
