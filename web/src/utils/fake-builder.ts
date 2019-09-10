import faker from 'faker';
import { Meter, Reading, Status } from '../typings/provider-data-interfaces';

function buildStatus(overrides?: Partial<Status>): Status {
  return {
    saving: faker.random.boolean(),
    changed: faker.random.boolean(),
    lastFetched: faker.date.recent(),
    saveError: null,
    ...overrides
  };
}

/**
 * Build a meter object that can be used in tests or stories.
 * @param overrides Override for random values
 */
export function buildMeter(overrides?: Partial<Meter>): Meter {
  return {
    id: faker.random.uuid(),
    meterNumber: faker.random.number({ min: 1000000000000000 }).toString(),
    ownerId: faker.random.uuid(),
    name: faker.company.companyName(),
    lastReading: buildReading(),
    type: faker.random.arrayElement(['gas', 'water', 'electricity']),
    status: buildStatus(),
    createdAt: faker.date.recent(),
    updatedAt: faker.date.recent(),
    deletedAt: null,
    ...overrides
  };
}

/**
 * Build a reading object that can be used in tests or stories.
 * @param overrides Override for random values
 */
export function buildReading(overrides?: Partial<Reading>): Reading {
  return {
    id: faker.random.uuid(),
    meterId: faker.random.uuid(),
    ownerId: faker.random.uuid(),
    lastEditorName: faker.name.firstName() + ' ' + faker.name.lastName(),
    lastEditReason: faker.lorem.words(5),
    trend: faker.random.number({ min: -10, max: 10 }),
    value: faker.random.number({ min: 1000000 }).toString(),
    status: buildStatus(),
    createdAt: faker.date.recent(),
    updatedAt: faker.date.recent(),
    deletedAt: null,
    ...overrides
  };
}
