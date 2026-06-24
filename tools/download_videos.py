#!/usr/bin/env python3
import os
import re
import subprocess
import sys

def get_youtube_links(md_file_path):
    """Parses solutions.md and extracts unique YouTube links from the Videos column."""
    if not os.path.exists(md_file_path):
        print(f"Error: {md_file_path} not found.")
        return []

    print(f"Parsing YouTube links from: {md_file_path}...")
    with open(md_file_path, "r", encoding="utf-8") as f:
        content = f.read()

    # Regex to capture YouTube watch URLs and short URLs
    youtube_pattern = re.compile(
        r"https?://(?:www\.)?(?:youtube\.com/watch\?v=|youtu\.be/)[a-zA-Z0-9_-]+"
    )
    
    links = youtube_pattern.findall(content)
    unique_links = list(dict.fromkeys(links))  # Remove duplicates while preserving order
    return unique_links

def download_videos(links):
    """Downloads listed YouTube videos using yt-dlp."""
    if not links:
        print("No videos found to download.")
        return

    print(f"Found {len(links)} unique YouTube video links.")
    
    # Create the output directory
    output_dir = "videos"
    os.makedirs(output_dir, exist_ok=True)
    print(f"Videos will be downloaded to: {os.path.abspath(output_dir)}\n")

    # Check if yt-dlp is installed
    try:
        subprocess.run(["yt-dlp", "--version"], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        has_ytdlp = True
    except FileNotFoundError:
        has_ytdlp = False

    if not has_ytdlp:
        print("Warning: 'yt-dlp' was not found on your system PATH.")
        print("Please install yt-dlp to run this downloader script:")
        print("  Mac (Homebrew):  brew install yt-dlp")
        print("  Windows:         winget install yt-dlp")
        print("  Python:          pip install yt-dlp")
        print("\nListing all extracted video URLs for reference:")
        for idx, url in enumerate(links, 1):
            print(f"  {idx}. {url}")
        return

    # Download each video with safe fallback parameters and proxy settings if necessary
    for idx, url in enumerate(links, 1):
        print(f"[{idx}/{len(links)}] Downloading: {url}...")
        
        # Build command. Supports corporate proxy if present in environments
        cmd = [
            "yt-dlp",
            "-o", f"{output_dir}/%(title)s.%(ext)s",
            "--no-playlist",
            url
        ]
        
        # Include proxy if configured in environment
        for env_var in ["HTTP_PROXY", "HTTPS_PROXY", "http_proxy", "https_proxy"]:
            if env_var in os.environ:
                cmd.extend(["--proxy", os.environ[env_var]])
                break

        try:
            subprocess.run(cmd, check=True)
            print(f"Successfully downloaded video {idx}!\n")
        except subprocess.CalledProcessError as e:
            print(f"Error downloading {url}: {e}\n")

def main():
    repo_root = "/Users/o0n00hg/Test/low-level-design-primer"
    md_file_path = os.path.join(repo_root, "solutions.md")
    links = get_youtube_links(md_file_path)
    download_videos(links)

if __name__ == "__main__":
    main()
