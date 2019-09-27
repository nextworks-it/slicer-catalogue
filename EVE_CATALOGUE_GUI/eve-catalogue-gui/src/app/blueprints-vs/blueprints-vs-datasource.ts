import { DataSource } from '@angular/cdk/collections';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { map } from 'rxjs/operators';
import { Observable, of as observableOf, merge } from 'rxjs';

// TODO: Replace this with your own data model type
export interface BlueprintsVsItem {
  id: number;
  version: string;
  name: string;
  description: string;
  confParams: string[];
  vsdNames: string[];
}

// TODO: replace this with real data from your application
const EXAMPLE_DATA: BlueprintsVsItem[] = [
  {
    id: 5,
    version: '1.0',
    name: 'ASTI AGV control and automation',
    description: 'AGVs using 5G network for Automation and Control purposes',
    confParams: ['max_AGVs', 'max_control_servers', 'avg_data_rate_per_AGV'],
    vsdNames: ['AGV_small_factory', 'AGV_medium_factory', 'AGV_large_factory']
  },
  {
    id: 20,
    version: '1.0',
    name: 'TRENITALIA Smart Transport with on-board media services',
    description: 'Delivery of high-quality media contents on high-speed trains',
    confParams: ['max_users', 'video_quality'],
    vsdNames: ['UHD_100_users', 'UHD_50_users', 'HD_500_users']
  },
  {
    id: 34,
    version: '1.0',
    name: 'Smart Turin: safety and environment monitoring for mobility service',
    description: 'Safety and environment monitoring with 5G for smart mobility service (people counting and mobility policies)',
    confParams: ['max_sensors', 'coverage_area', 'max_latency_per_sensor', 'avg_polling_frequency'],
    vsdNames: ['High_density_district', 'Medium_density_district', 'Low_density_district']
  }
];

/**
 * Data source for the BlueprintsVs view. This class should
 * encapsulate all logic for fetching and manipulating the displayed data
 * (including sorting, pagination, and filtering).
 */
export class BlueprintsVsDataSource extends DataSource<BlueprintsVsItem> {
  data: BlueprintsVsItem[] = EXAMPLE_DATA;
  paginator: MatPaginator;
  sort: MatSort;

  constructor() {
    super();
  }

  /**
   * Connect this data source to the table. The table will only update when
   * the returned stream emits new items.
   * @returns A stream of the items to be rendered.
   */
  connect(): Observable<BlueprintsVsItem[]> {
    // Combine everything that affects the rendered data into one update
    // stream for the data-table to consume.
    const dataMutations = [
      observableOf(this.data),
      this.paginator.page,
      this.sort.sortChange
    ];

    return merge(...dataMutations).pipe(map(() => {
      return this.getPagedData(this.getSortedData([...this.data]));
    }));
  }

  /**
   *  Called when the table is being destroyed. Use this function, to clean up
   * any open connections or free any held resources that were set up during connect.
   */
  disconnect() {}

  /**
   * Paginate the data (client-side). If you're using server-side pagination,
   * this would be replaced by requesting the appropriate data from the server.
   */
  private getPagedData(data: BlueprintsVsItem[]) {
    const startIndex = this.paginator.pageIndex * this.paginator.pageSize;
    return data.splice(startIndex, this.paginator.pageSize);
  }

  /**
   * Sort the data (client-side). If you're using server-side sorting,
   * this would be replaced by requesting the appropriate data from the server.
   */
  private getSortedData(data: BlueprintsVsItem[]) {
    if (!this.sort.active || this.sort.direction === '') {
      return data;
    }

    return data.sort((a, b) => {
      const isAsc = this.sort.direction === 'asc';
      switch (this.sort.active) {
        case 'name': return compare(a.name, b.name, isAsc);
        case 'id': return compare(+a.id, +b.id, isAsc);
        default: return 0;
      }
    });
  }
}

/** Simple sort comparator for example ID/Name columns (for client-side sorting). */
function compare(a, b, isAsc) {
  return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
}
