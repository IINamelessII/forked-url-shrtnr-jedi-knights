"""Collect Data Script"""

import csv
import os
from pathlib import Path


if __name__ == '__main__':

    # cleanup before work
    for fph in Path('stats').glob('*'):
        fph.unlink()

    # increase QPS iteratively
    qps = 5

    # number of error rate > 1% in a row
    error_fail_in_a_row = 0

    while True:
        print(f'Running at {qps} QPS')

        # run load testing
        os.system(
            f'locust -f locustfile.py --headless -u {qps} -r {qps} '
            f'--run-time 30s --csv stats/{qps} --only-summary'
        )

        # make correct file
        with open(f'stats/{qps}_stats.csv', 'r') as inp, open(f'stats/{qps}.csv', 'w') as out:
            writter = csv.writer(out)
            rows = tuple(csv.reader(inp))
            desired = {0, len(rows) - 1}
            for idx, row in enumerate(rows):
                if idx in desired:
                    writter.writerow(row)

        # remove redundant files
        for suffix in ('stats', 'exceptions', 'failures', 'stats_history'):
            Path(f'stats/{qps}_{suffix}.csv').unlink()

        # stats of current run
        with open(f'stats/{qps}.csv', 'r') as inp:
            row = tuple(csv.reader(inp))[1]

        print(f'INFO: 99% - {row[18]}')

        # if 99% > 1000ms
        if int(row[18]) > 1000:
            print('Exit: 99% > 1000ms')
            break

        if int(row[3]) / (int(row[2]) or 1) > 0.01:
            error_fail_in_a_row += 1
        else:
            error_fail_in_a_row = 0

        if error_fail_in_a_row == 3:
            print('Exit: 3 Error Rate > 1% in a row')
            break

        qps += 5
