import matplotlib.pyplot as plt
import csv
import os
import re
from pathlib import Path

from utils import convert_to_int, extract_csv


if __name__ == '__main__':

    BASE_DIR = Path(__file__).parent.parent.absolute()
    STATS_PATH = f'{BASE_DIR}/stats'
    IMG_CHARTS_PATH = f'{BASE_DIR}/charts/img'

    raw_stats_list = extract_csv(STATS_PATH)

    # Convert general data to dict { <latency>: (<QPS>, <Response Time>) }
    latency_stats = {
        "50%": [],
        "90%": [],
        "99%": [],
    }

    for percentile in latency_stats.keys():
        latency_stats[percentile] = sorted([
            (round(float(item['Requests/s'])),
             convert_to_int(item[percentile]))
            for item in raw_stats_list
        ], key=lambda el: el[0])

    # Build line charts for each percentile
    fig, axs = plt.subplots(3)
    fig.suptitle('Load Testing')

    fig.add_subplot(111, frameon=False)
    plt.tick_params(labelcolor='none', top=False,
                    bottom=False, left=False, right=False)
    plt.grid(False)

    plt.xlabel('QPS')
    plt.ylabel('Response Time')

    color = ['maroon', 'steelblue', 'seagreen']
    for (idx, (percentile, data)) in enumerate(latency_stats.items()):
        axs[idx].plot(*list(zip(*data)), marker='.', color=color[idx])
        axs[idx].set_title(percentile)
        axs[idx].grid()

    fig.tight_layout()
    plt.savefig(f'{IMG_CHARTS_PATH}/load_testing_chart.png')
