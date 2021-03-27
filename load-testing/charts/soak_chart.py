import matplotlib.pyplot as plt
import csv
import os
import re
from pathlib import Path

from utils import convert_to_int, extract_csv


def build_chart(
        raw_data: dict,
        xlabel: str,
        ylabel: str,
        path: str):
    """Sh*t-coded chart building function (written just for temporary usage)

    Args:
        raw_data (dict): Dict with lists of paired data
        chart_title (str): Title should be displayed on the top of a chart
        xlabel (str): Label for x axis
        ylabel (str): Label for y axis
        filename (str): Name of file with chart(s)
    """
    
    chart_count = len(raw_data.keys())
    fig, axs = plt.subplots(chart_count)
    fig.suptitle('Soak Testing')

    fig.add_subplot(111, frameon=False)
    plt.tick_params(labelcolor='none', top=False,
                    bottom=False, left=False, right=False)
    plt.grid(False)

    plt.xlabel(xlabel)
    plt.ylabel(ylabel)

    color = ['maroon', 'steelblue', 'seagreen']
    if chart_count > 1:
        for (idx, (title, data)) in enumerate(raw_data.items()):
            axs[idx].plot(*list(zip(*data)), marker='.', color=color[idx])
            axs[idx].set_title(title)
            axs[idx].grid()
    else:
        title = list(raw_data.keys())[0]
        axs.plot(*list(zip(*raw_data[title])), marker='.', color='maroon')
        axs.set_title(title)
        axs.grid()

    fig.tight_layout()
    plt.savefig(path)


if __name__ == '__main__':

    BASE_DIR = Path(__file__).parent.parent.absolute()
    SOAK_STATS_PATH = f'{BASE_DIR}/stats/soak'
    IMG_CHARTS_PATH = f'{BASE_DIR}/charts/img'


    raw_soak_stats_list = extract_csv(SOAK_STATS_PATH, is_soak=True)


    lat_vs_time = {
        "50%": [],
        "90%": [],
        "99%": [],
    }

    fails_vs_time = {
        "Failures/s": [],
    }


    # Convert raw data to dict { <latency>: (<Time range>, <Response Time>) }
    for percentile in lat_vs_time.keys():
        lat_vs_time[percentile] = sorted([
            (convert_to_int(item['Time']),
             convert_to_int(item[percentile]))
            for item in raw_soak_stats_list
        ], key=lambda el: el[0])

    # Convert raw data to dict { <fails>: (<Time range>, <Failures/s>) }
    for percentile in fails_vs_time.keys():
        fails_vs_time[percentile] = sorted([
            (convert_to_int(item['Time']),
             float(item['Failures/s']))
            for item in raw_soak_stats_list
        ], key=lambda el: el[0])


    build_chart(lat_vs_time,
                'Time Range',
                'Response Time',
                f'{IMG_CHARTS_PATH}/soak_testing.png')
    build_chart(fails_vs_time,
                'Time Range',
                'Failures/s',
                f'{IMG_CHARTS_PATH}/soak_testing_failures.png')
